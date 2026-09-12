package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.AudioMusicaDao;
import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dto.AudioMusicaDTO;
import com.baseapplication.core.dto.QuotaAudioDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.AudioMusica;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AudioMusicaService;
import com.baseapplication.core.service.TranscodificadorAudio;
import com.baseapplication.core.utils.ChaveMusica;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AudioMusicaServiceImpl implements AudioMusicaService {

    private static final String BUCKET = "setlistaudio";

    private final AudioMusicaDao audioMusicaDao;
    private final MusicoBandaDao musicoBandaDao;
    private final MinioStorageServiceImpl minioStorageService;
    private final TranscodificadorAudio transcodificador;

    /**
     * 500MB por banda: ~87 musicas de 4min a 192k (5,76MB cada, medido).
     *
     * <p>Dimensionado para o repertorio completo de uma banda, nao so para os setlists que vao
     * ao palco. Com menos de 20 bandas o pior caso sao 10GB, o que cabe folgado -- mas o teto
     * e linear no numero de bandas, entao esta constante e a que segura o disco da VPS.
     *
     * <p>Quando apertar, e com que gatilho: {@code docs/storage-audio-vs.md}.
     */
    @Value("${audio.quota-bytes-por-banda:524288000}")
    private long quotaBytesPorBanda;

    // ------------------------------------------------------------------
    // Leitura
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<AudioMusicaDTO> listarPorBanda(Long idBanda) {
        exigirMembro(idBanda);
        return audioMusicaDao.findByIdBanda(idBanda).stream()
                .map(AudioMusicaDTO::new)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public QuotaAudioDTO consultarQuota(Long idBanda) {
        exigirMembro(idBanda);
        List<AudioMusica> audios = audioMusicaDao.findByIdBanda(idBanda);
        long usado = audios.stream().mapToLong(a -> a.getTamanhoBytes() == null ? 0 : a.getTamanhoBytes()).sum();
        return new QuotaAudioDTO(usado, quotaBytesPorBanda, audios.size());
    }

    // ------------------------------------------------------------------
    // Escrita
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public AudioMusicaDTO enviar(Long idBanda, String titulo, String artista, MultipartFile arquivo) {
        exigirMembro(idBanda);

        if (arquivo == null || arquivo.isEmpty()) {
            throw new InvalidParamException("Nenhum arquivo de áudio foi enviado.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new InvalidParamException("O áudio precisa estar ligado a uma música com título.");
        }

        String chave = ChaveMusica.de(titulo, artista);
        Optional<AudioMusica> existente = audioMusicaDao.findByIdBandaAndChaveMusica(idBanda, chave);

        TranscodificadorAudio.AudioProcessado processado = transcodificador.processar(arquivo);

        // A quota e conferida com o tamanho final, nao com o do upload: um WAV de 40MB que
        // vira um MP3 de 5MB nao deve ser barrado por 40. Uma substituicao devolve o espaco
        // do arquivo que vai embora antes de somar o novo.
        long usado = audioMusicaDao.somarBytesDaBanda(idBanda);
        long liberado = existente.map(a -> a.getTamanhoBytes() == null ? 0L : a.getTamanhoBytes()).orElse(0L);
        if (usado - liberado + processado.bytes().length > quotaBytesPorBanda) {
            throw new RestrictionException(
                    "A banda atingiu o limite de " + (quotaBytesPorBanda / (1024 * 1024))
                            + " MB de áudios. Remova algum VS antes de enviar outro.");
        }

        String sha = sha256(processado.bytes());

        // A chave do objeto e o proprio hash: o mesmo arquivo em duas musicas da banda grava
        // no mesmo lugar em vez de duplicar, e o putObject vira idempotente.
        String nomeObjeto = "banda-" + idBanda + "/" + sha + processado.extensao();
        String url = minioStorageService.uploadBytes(
                processado.bytes(), BUCKET, nomeObjeto, processado.contentType());

        AudioMusica audio = existente.orElseGet(AudioMusica::new);
        String urlAnterior = audio.getUrl();

        audio.setIdBanda(idBanda);
        audio.setChaveMusica(chave);
        audio.setTitulo(titulo);
        audio.setArtista(artista);
        audio.setUrl(url);
        audio.setNomeOriginal(arquivo.getOriginalFilename());
        audio.setDuracaoSeg(processado.duracaoSeg());
        audio.setTamanhoBytes((long) processado.bytes().length);
        audio.setSha256(sha);
        audio.setIdUsuarioUpload(idUsuarioLogado());
        if (audio.getId() == null) {
            audio.setDataCriacao(LocalDateTime.now());
        }

        audioMusicaDao.save(audio);

        // Troca de arquivo: o objeto antigo so sai depois que a linha ja aponta para o novo.
        // Na ordem inversa, uma falha no meio deixaria a tela apontando para um objeto que nao
        // existe mais -- pior que deixar lixo no bucket.
        if (urlAnterior != null && !urlAnterior.equals(url)) {
            apagarObjetoSeOrfao(urlAnterior);
        }

        return new AudioMusicaDTO(audio);
    }

    @Override
    @Transactional
    public void remover(Long idBanda, Long idAudio) {
        exigirMembro(idBanda);

        AudioMusica audio = audioMusicaDao.findById(idAudio)
                .orElseThrow(() -> new ResourceNotFoundException("Áudio não encontrado"));

        // Sem esta checagem, o id no path seria suficiente para apagar o VS de outra banda.
        if (!audio.getIdBanda().equals(idBanda)) {
            throw new RestrictionException("Este áudio não pertence a esta banda");
        }

        String url = audio.getUrl();
        audioMusicaDao.delete(audio);
        audioMusicaDao.flush(); // o countByUrl abaixo precisa enxergar a linha ja removida
        apagarObjetoSeOrfao(url);
    }

    @Override
    @Transactional
    public void renomear(Long idBanda, String tituloAntigo, String artistaAntigo,
                         String tituloNovo, String artistaNovo) {
        String chaveAntiga = ChaveMusica.de(tituloAntigo, artistaAntigo);
        String chaveNova = ChaveMusica.de(tituloNovo, artistaNovo);
        if (chaveAntiga.equals(chaveNova)) {
            return;
        }

        audioMusicaDao.findByIdBandaAndChaveMusica(idBanda, chaveAntiga).ifPresent(audio -> {
            // Se a musica foi renomeada para o nome de outra que ja tem VS, o unique de
            // (banda, chave) estouraria. Renomear e uma acao sobre a musica, nao sobre o audio:
            // melhor deixar o VS onde esta -- alcancavel pela tela de audios -- do que derrubar
            // a edicao do repertorio com um erro que o usuario nao sabe interpretar.
            if (audioMusicaDao.findByIdBandaAndChaveMusica(idBanda, chaveNova).isPresent()) {
                log.warn("VS da banda {} nao migrou de '{}' para '{}': o destino ja tem audio.",
                        idBanda, chaveAntiga, chaveNova);
                return;
            }
            audio.setChaveMusica(chaveNova);
            audio.setTitulo(tituloNovo);
            audio.setArtista(artistaNovo);
            audioMusicaDao.save(audio);
        });
    }

    // ------------------------------------------------------------------

    /**
     * Qualquer membro sobe e remove VS -- de proposito. Quem tem o arquivo e o baterista, que
     * raramente e administrador da banda; exigir permissao de gestao aqui inviabilizaria a
     * feature para quem ela serve.
     */
    private void exigirMembro(Long idBanda) {
        Usuario logado = Context.getUsuarioLogado();
        if (logado == null || logado.getId() == null) {
            throw new RestrictionException("Usuário não autenticado");
        }
        musicoBandaDao.buscarMembro(idBanda, logado.getId())
                .orElseThrow(() -> new RestrictionException("Você não faz parte desta banda"));
    }

    private Long idUsuarioLogado() {
        Usuario logado = Context.getUsuarioLogado();
        return logado == null ? null : logado.getId();
    }

    private void apagarObjetoSeOrfao(String url) {
        if (url == null || url.isBlank()) return;
        if (audioMusicaDao.countByUrl(url) > 0) return;
        minioStorageService.deleteImageByUrl(url);
    }

    private String sha256(byte[] conteudo) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(conteudo));
        } catch (NoSuchAlgorithmException e) {
            throw new InternalException("SHA-256 indisponível nesta JVM");
        }
    }
}
