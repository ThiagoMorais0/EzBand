package com.baseapplication.core.service.impl;

import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.service.TranscodificadorAudio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Normaliza o arquivo que o usuario enviou para um MP3 que o navegador toca e que cabe no
 * storage, sem perder a separacao entre os canais.
 *
 * <p>Precisa de {@code ffmpeg} e {@code ffprobe}, obtidos de um de dois jeitos:
 *
 * <ul>
 *   <li><b>Do host</b> (padrão, e o que roda em produção): os binários vêm do PATH, instalados
 *       na imagem da API pelo {@code apk add ffmpeg} do Dockerfile.</li>
 *   <li><b>De um container efêmero</b>: com {@code audio.docker.image} preenchido, cada chamada
 *       roda num {@code docker run --rm}. É para o desenvolvimento local, onde o backend roda
 *       fora do Docker e instalar ffmpeg no Windows custa mexer no PATH e reiniciar a IDE.</li>
 * </ul>
 *
 * <p>O resto da classe não sabe qual dos dois está em uso -- a diferença mora inteira em
 * {@link #montarComando}.
 */
@Slf4j
@Service
public class TranscodificadorAudioImpl implements TranscodificadorAudio {

    /** MP3/AAC ate este tamanho passam intactos: re-encodar lossy sobre lossy so piora. */
    private static final long LIMITE_SEM_TRANSCODIFICAR_BYTES = 15L * 1024 * 1024;

    private static final long TIMEOUT_ENCODE_SEGUNDOS = 120;
    private static final long TIMEOUT_SONDA_SEGUNDOS = 30;

    /**
     * Folga acrescentada aos dois timeouts quando a chamada roda em container.
     *
     * <p>Medido no Docker Desktop/Windows: criar um container custa ~6,5s **antes** de o
     * ffmpeg começar -- mais que o ffprobe inteiro leva sobre um WAV de 96MB (6,7s no total).
     * Os timeouts acima foram dimensionados para execução nativa e ficariam apertados com esse
     * pedágio, ainda mais com o Docker recém-acordado, quando o primeiro run é bem mais lento.
     */
    private static final long FOLGA_DOCKER_SEGUNDOS = 60;

    private static final List<String> CODECS_JA_ACEITOS = List.of("mp3", "aac");

    @Value("${audio.bitrate:192k}")
    private String bitrate;

    /*
     * Só o nome do executável por padrão, resolvido pelo PATH -- que é como a imagem Docker
     * entrega os dois, via `apk add ffmpeg`.
     *
     * Existem como propriedade por causa do desenvolvimento no Windows: lá o instalador
     * frequentemente não mexe no PATH, e mesmo quando mexe a IDE já está aberta com o PATH
     * antigo herdado. Apontar o caminho absoluto no application.properties resolve sem
     * precisar reiniciar nada.
     */
    @Value("${audio.ffmpeg:ffmpeg}")
    private String ffmpeg;

    @Value("${audio.ffprobe:ffprobe}")
    private String ffprobe;

    /**
     * Imagem Docker que fornece ffmpeg/ffprobe. Vazio (o padrão) = usa os binários do host.
     *
     * <p>Em produção fica vazio: a imagem da API já traz os dois via {@code apk add}, e subir
     * um container por upload dentro de outro container seria absurdo. Isto existe para o
     * desenvolvimento local, onde o backend roda fora do Docker (mvn spring-boot:run) e
     * instalar ffmpeg no Windows significa mexer no PATH e reiniciar a IDE.
     */
    @Value("${audio.docker.image:}")
    private String dockerImage;

    /**
     * Onde os temporários são criados. Só este diretório é montado no container, então ele
     * precisa ser um lugar só nosso -- montar o {@code java.io.tmpdir} inteiro exporia todo
     * arquivo temporário da JVM a um processo externo sem necessidade nenhuma.
     */
    @Value("${audio.trabalho-dir:}")
    private String trabalhoDirConfigurado;

    private static final String MONTAGEM_NO_CONTAINER = "/work";

    private boolean viaDocker() {
        return dockerImage != null && !dockerImage.isBlank();
    }

    private Path trabalhoDir() throws IOException {
        Path dir = trabalhoDirConfigurado == null || trabalhoDirConfigurado.isBlank()
                ? Path.of(System.getProperty("java.io.tmpdir"), "ezband-audio")
                : Path.of(trabalhoDirConfigurado);
        Files.createDirectories(dir);
        return dir;
    }

    @Override
    public AudioProcessado processar(MultipartFile arquivo) {
        Path entrada = null;
        Path saida = null;
        try {
            Path dir = trabalhoDir();
            entrada = Files.createTempFile(dir, "vs-in-", sufixoDe(arquivo.getOriginalFilename()));
            // transferTo com um File relativo resolveria contra o diretorio temporario do
            // multipart, nao contra o nosso -- toAbsolutePath evita escrever fora do que sera
            // montado no container.
            arquivo.transferTo(entrada.toAbsolutePath());

            Sonda sonda = sondar(entrada);
            if (sonda.codec == null) {
                throw new InvalidParamException(
                        "O arquivo enviado não tem faixa de áudio. Envie um MP3, WAV, FLAC ou M4A.");
            }

            // Um MP3 que ja veio pronto da DAW passa direto: re-encodar lossy sobre lossy so
            // perde qualidade, e o arquivo ja esta no formato e no tamanho que queremos.
            if (CODECS_JA_ACEITOS.contains(sonda.codec) && arquivo.getSize() <= LIMITE_SEM_TRANSCODIFICAR_BYTES) {
                return new AudioProcessado(Files.readAllBytes(entrada),
                        contentTypeDe(sonda.codec),
                        extensaoDe(sonda.codec),
                        sonda.duracaoSeg);
            }

            saida = Files.createTempFile(dir, "vs-out-", ".mp3");
            transcodificar(entrada, saida);

            byte[] bytes = Files.readAllBytes(saida);
            log.info("VS transcodificado: {} ({} bytes, codec {}) -> mp3 {} ({} bytes)",
                    arquivo.getOriginalFilename(), arquivo.getSize(), sonda.codec, bitrate, bytes.length);

            return new AudioProcessado(bytes, "audio/mpeg", ".mp3", sonda.duracaoSeg);

        } catch (IOException e) {
            throw new InternalException("Erro ao processar o arquivo de áudio: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalException("Processamento do áudio interrompido");
        } finally {
            apagar(entrada);
            apagar(saida);
        }
    }

    // ------------------------------------------------------------------

    private void transcodificar(Path entrada, Path saida) throws IOException, InterruptedException {
        Resultado resultado = executar(TIMEOUT_ENCODE_SEGUNDOS,
                ffmpeg, "-y",
                "-i", entrada.toString(),
                "-vn",                       // capa de album embutida nao vira faixa de video
                "-map_metadata", "-1",       // tags da DAW podem trazer caminho de arquivo local
                "-c:a", "libmp3lame",
                "-b:a", bitrate,
                // O unico parametro de canal que importa. Um VS mono viraria estereo falso (o
                // mesmo click nos dois ouvidos, sem playback) e um 5.1 seria dobrado errado.
                "-ac", "2",
                saida.toString());

        // Nao ha "-joint_stereo 0" aqui, e e de proposito.
        //
        // A intuicao diz que joint stereo ameaca o VS: o encoder guarda meio/lado em vez dos
        // dois canais, entao o click de um lado poderia vazar no playback do outro. Medimos,
        // com L = click de 2kHz pulsado e R = acorde grave, isolando o canal R e filtrando a
        // banda do click. O vazamento deu -57.9 dB a 96k, -57.9 a 128k e -57.8 a 192k -- o
        // proprio piso de ruido do filtro -- e identico com a flag ligada e desligada, com
        // arquivos do mesmo tamanho byte a byte nos tres bitrates.
        //
        // O motivo e que o LAME decide M/S por banda e por quadro: canais descorrelacionados,
        // que e exatamente o caso de um VS, ja fazem ele escolher L/R independente sozinho.
        // Forcar dual channel so tiraria bits do encoder sem separar nada que ja nao esteja
        // separado.

        if (resultado.timeout) {
            throw new InternalException("A conversão do áudio demorou demais e foi cancelada." + dicaDoPull());
        }
        if (resultado.exitCode != 0) {
            log.error("ffmpeg falhou (exit {}): {}", resultado.exitCode, resultado.saida);
            throw new InvalidParamException("Não foi possível converter este arquivo de áudio.");
        }
    }

    /** Codec e duracao em uma chamada so; ffprobe e barato comparado ao encode. */
    private Sonda sondar(Path arquivo) throws IOException, InterruptedException {
        Resultado resultado = executar(TIMEOUT_SONDA_SEGUNDOS,
                ffprobe, "-v", "error",
                "-select_streams", "a:0",
                "-show_entries", "stream=codec_name:format=duration",
                "-of", "default=noprint_wrappers=1:nokey=0",
                arquivo.toString());

        if (resultado.timeout) {
            throw new InternalException("A leitura do arquivo de áudio demorou demais." + dicaDoPull());
        }

        Sonda sonda = new Sonda();
        for (String linha : resultado.saida.split("\\R")) {
            int igual = linha.indexOf('=');
            if (igual < 0) continue;
            String chave = linha.substring(0, igual).trim();
            String valor = linha.substring(igual + 1).trim();
            if ("codec_name".equals(chave)) {
                sonda.codec = valor.toLowerCase(Locale.ROOT);
            } else if ("duration".equals(chave) && !"N/A".equals(valor)) {
                try {
                    sonda.duracaoSeg = (int) Math.round(Double.parseDouble(valor));
                } catch (NumberFormatException ignored) {
                    // Container sem duracao declarada: a tela mostra o audio sem o tempo total.
                }
            }
        }
        return sonda;
    }

    /**
     * Roda o processo com a saida redirecionada para arquivo, nao para pipe.
     *
     * <p>Ler o pipe com {@code readAllBytes} antes do {@code waitFor} anularia o timeout --
     * a leitura so retorna no EOF, ou seja, quando o processo ja morreu; um ffmpeg travado
     * prenderia a thread para sempre. Com arquivo, o {@code waitFor} e quem manda, e a saida
     * fica disponivel depois para a mensagem de erro.
     */
    private Resultado executar(long timeoutBase, String... comando) throws IOException, InterruptedException {
        long timeoutSegundos = viaDocker() ? timeoutBase + FOLGA_DOCKER_SEGUNDOS : timeoutBase;
        List<String> linhaDeComando = montarComando(comando);
        Path logProcesso = Files.createTempFile(trabalhoDir(), "vs-log-", ".txt");
        try {
            Process processo;
            try {
                processo = new ProcessBuilder(linhaDeComando)
                        .redirectErrorStream(true)
                        .redirectOutput(logProcesso.toFile())
                        .start();
            } catch (IOException e) {
                // Binario ausente chega aqui como um IOException generico ("CreateProcess
                // error=2" no Windows, "No such file or directory" no Linux) que, propagado
                // cru, vira "erro ao processar o arquivo" na tela -- e manda quem esta
                // desenvolvendo procurar defeito no upload em vez de no ambiente.
                String faltando = viaDocker() ? "docker" : comando[0];
                throw new InternalException(
                        "O conversor de áudio (" + faltando + ") não foi encontrado. "
                                + (viaDocker()
                                        ? "audio.docker.image está configurado, então o Docker precisa estar instalado e rodando."
                                        : "Em produção ele vem na imagem Docker; para rodar o backend local, "
                                                + "defina audio.docker.image ou instale o ffmpeg no host.")
                                + " Detalhe: " + e.getMessage());
            }

            if (!processo.waitFor(timeoutSegundos, TimeUnit.SECONDS)) {
                processo.destroyForcibly();
                return new Resultado(-1, "", true);
            }
            return new Resultado(processo.exitValue(),
                    Files.readString(logProcesso, StandardCharsets.UTF_8),
                    false);
        } finally {
            apagar(logProcesso);
        }
    }

    /**
     * Traduz a chamada para o jeito configurado: binário do host, ou o mesmo binário dentro de
     * um container efêmero.
     *
     * <p>No modo Docker o diretório de trabalho é montado em {@code /work} e todo argumento que
     * aponta para dentro dele é reescrito para o caminho equivalente no container. Só os
     * caminhos passam por tradução -- {@code -b:a}, {@code 192k} e afins seguem intactos.
     *
     * <p>{@code --entrypoint} é obrigatório: as imagens de ffmpeg já apontam o entrypoint para
     * o próprio ffmpeg, então sem sobrescrever não há como chamar o ffprobe.
     */
    private List<String> montarComando(String... comando) throws IOException {
        if (!viaDocker()) {
            return List.of(comando);
        }

        Path dir = trabalhoDir().toAbsolutePath();
        // O Docker Desktop no Windows aceita "C:/caminho", não "C:\caminho".
        String dirHost = dir.toString().replace('\\', '/');

        List<String> linha = new ArrayList<>(List.of(
                "docker", "run", "--rm",
                "-v", dirHost + ":" + MONTAGEM_NO_CONTAINER,
                "--entrypoint", nomeDoBinario(comando[0]),
                dockerImage));

        for (int i = 1; i < comando.length; i++) {
            linha.add(traduzirCaminho(comando[i], dir));
        }
        return linha;
    }

    /**
     * O container traz o binário no PATH com o nome simples. Se o host configurou um caminho
     * absoluto (C:/ffmpeg/bin/ffmpeg.exe), é o nome que interessa lá dentro.
     */
    private String nomeDoBinario(String caminhoOuNome) {
        String nome = Path.of(caminhoOuNome).getFileName().toString();
        return nome.endsWith(".exe") ? nome.substring(0, nome.length() - 4) : nome;
    }

    private String traduzirCaminho(String argumento, Path dirTrabalho) {
        Path comoPath;
        try {
            comoPath = Path.of(argumento);
        } catch (Exception e) {
            return argumento; // "-b:a" e companhia nao sao caminho em todo sistema de arquivos
        }
        if (!comoPath.isAbsolute() || !comoPath.startsWith(dirTrabalho)) {
            return argumento;
        }
        return MONTAGEM_NO_CONTAINER + "/"
                + dirTrabalho.relativize(comoPath).toString().replace('\\', '/');
    }

    /**
     * A primeira conversão numa máquina nova baixa a imagem (~80MB) dentro do timeout, e é de
     * longe a causa mais provável de um estouro no modo Docker. Dizer isso na mensagem evita
     * que alguém vá investigar o arquivo de áudio.
     */
    private String dicaDoPull() {
        return viaDocker()
                ? " Se for a primeira conversão nesta máquina, a imagem ainda está sendo baixada:"
                        + " rode `docker pull " + dockerImage + "` e tente de novo."
                : "";
    }

    private String sufixoDe(String nomeOriginal) {
        if (nomeOriginal == null) return ".tmp";
        int ponto = nomeOriginal.lastIndexOf('.');
        // ffmpeg usa a extensao como dica de container; sem ela ainda funciona pelo conteudo.
        return ponto > 0 && ponto < nomeOriginal.length() - 1 ? nomeOriginal.substring(ponto) : ".tmp";
    }

    private String contentTypeDe(String codec) {
        return "aac".equals(codec) ? "audio/mp4" : "audio/mpeg";
    }

    private String extensaoDe(String codec) {
        return "aac".equals(codec) ? ".m4a" : ".mp3";
    }

    private void apagar(Path caminho) {
        if (caminho == null) return;
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            log.warn("Não foi possível apagar o temporário {}: {}", caminho, e.getMessage());
        }
    }

    private static class Sonda {
        String codec;
        Integer duracaoSeg;
    }

    private record Resultado(int exitCode, String saida, boolean timeout) {
    }
}
