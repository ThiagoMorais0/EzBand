package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.RepertorioBandaDao;
import com.baseapplication.core.dto.MusicaSugeridaDTO;
import com.baseapplication.core.dto.ResultadoSugestaoRepertorioDTO;
import com.baseapplication.core.dto.SugestaoRepertorioDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.service.SugestaoRepertorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SugestaoRepertorioServiceImpl implements SugestaoRepertorioService {
    
    private final RepertorioBandaDao repertorioBandaDao;
    
    @Override
    public ResultadoSugestaoRepertorioDTO sugerirRepertorio(SugestaoRepertorioDTO dto) {
        // Buscar todas as músicas do repertório da banda
        List<RepertorioBanda> todasMusicas = repertorioBandaDao.findByBandaId(dto.getIdBanda());
        
        if (todasMusicas.isEmpty()) {
            throw new InternalException("A banda não possui músicas no repertório");
        }
        
        // Filtrar músicas que possuem duração definida
        List<RepertorioBanda> musicasComDuracao = todasMusicas.stream()
                .filter(m -> m.getMusica().getDuracao() != null)
                .collect(Collectors.toList());
        
        if (musicasComDuracao.isEmpty()) {
            throw new InternalException("Nenhuma música possui duração definida");
        }
        
        // Executar algoritmo de seleção (converter minutos para segundos)
        List<RepertorioBanda> musicasSelecionadas = selecionarMusicas(
                musicasComDuracao, 
                dto.getDuracaoShowMinutos() * 60
        );
        
        // Ordenar músicas: por curva de energia (se fornecida) ou por posição no show
        List<RepertorioBanda> musicasOrdenadas;
        if (dto.getCurvaEnergia() != null && !dto.getCurvaEnergia().isEmpty()) {
            musicasOrdenadas = ordenarPorCurvaEnergia(musicasSelecionadas, dto.getCurvaEnergia());
        } else {
            musicasOrdenadas = ordenarPorPosicaoShow(musicasSelecionadas);
        }
        
        // Calcular duração total
        int duracaoTotal = calcularDuracaoTotal(musicasOrdenadas);
        
        // Converter para DTO
        List<MusicaSugeridaDTO> musicasSugeridas = new ArrayList<>();
        for (int i = 0; i < musicasOrdenadas.size(); i++) {
            RepertorioBanda musica = musicasOrdenadas.get(i);
            double pontuacao = calcularPontuacao(musica, i, musicasOrdenadas.size());
            musicasSugeridas.add(new MusicaSugeridaDTO(musica, i + 1, pontuacao));
        }
        
        // Montar resultado (converter segundos de volta para minutos)
        ResultadoSugestaoRepertorioDTO resultado = new ResultadoSugestaoRepertorioDTO();
        resultado.setMusicasSugeridas(musicasSugeridas);
        resultado.setDuracaoTotalMinutos((int) Math.ceil(duracaoTotal / 60.0));
        resultado.setDuracaoSolicitadaMinutos(dto.getDuracaoShowMinutos());
        resultado.setQuantidadeMusicas(musicasSugeridas.size());
        resultado.setMensagem(gerarMensagem((int) Math.ceil(duracaoTotal / 60.0), dto.getDuracaoShowMinutos()));
        
        return resultado;
    }
    
    /**
     * FASE 1: Seleção de músicas baseada APENAS em relevância e duração
     * Objetivo: Escolher quais músicas vão para o show
     * Critérios:
     * 1. Relevância (prioriza músicas com alta relevância)
     * 2. Duração (não ultrapassa o tempo do show)
     * 3. Aleatoriedade entre músicas de mesma relevância
     */
    private List<RepertorioBanda> selecionarMusicas(List<RepertorioBanda> musicas, Integer duracaoMaximaSegundos) {
        // Ordenar todas as músicas por relevância (decrescente) com aleatoriedade
        List<RepertorioBanda> musicasOrdenadas = new ArrayList<>(musicas);
        ordenarPorRelevanciaComAleatoriedade(musicasOrdenadas);
        
        // Selecionar músicas até atingir o limite de duração
        List<RepertorioBanda> selecionadas = new ArrayList<>();
        int duracaoAtual = 0;
        
        for (RepertorioBanda musica : musicasOrdenadas) {
            int duracaoMusica = timeToSeconds(musica.getMusica().getDuracao());
            
            // Verifica se ainda cabe no tempo
            if (duracaoAtual + duracaoMusica <= duracaoMaximaSegundos) {
                selecionadas.add(musica);
                duracaoAtual += duracaoMusica;
            }
        }
        
        return selecionadas;
    }
    
    /**
     * Ordena músicas por relevância (decrescente), mas embaralha aquelas com mesma relevância
     * Usado na FASE 1 (Seleção)
     */
    private void ordenarPorRelevanciaComAleatoriedade(List<RepertorioBanda> musicas) {
        // Agrupar por relevância
        Map<Integer, List<RepertorioBanda>> musicasPorRelevancia = musicas.stream()
                .collect(Collectors.groupingBy(this::obterRelevancia));
        
        // Limpar lista original
        musicas.clear();
        
        // Ordenar por relevância (decrescente) e embaralhar músicas de mesma relevância
        musicasPorRelevancia.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .forEach(relevancia -> {
                    List<RepertorioBanda> musicasMesmaRelevancia = musicasPorRelevancia.get(relevancia);
                    Collections.shuffle(musicasMesmaRelevancia);
                    musicas.addAll(musicasMesmaRelevancia);
                });
    }
    
    
    /**
     * FASE 2: Ordenação das músicas selecionadas por posição no show
     * Objetivo: Definir a ordem de execução no show
     * Critério: Posição exata no show (1 a 10)
     * 
     * Músicas são ordenadas pela posição crescente (1, 2, 3... 10)
     * Músicas com mesma posição são embaralhadas para adicionar variedade
     */
    private List<RepertorioBanda> ordenarPorPosicaoShow(List<RepertorioBanda> musicas) {
        // Agrupar músicas por posição
        Map<Integer, List<RepertorioBanda>> musicasPorPosicao = musicas.stream()
                .collect(Collectors.groupingBy(this::obterPosicaoShow));
        
        // Ordenar por posição crescente e embaralhar músicas de mesma posição
        List<RepertorioBanda> resultado = new ArrayList<>();
        musicasPorPosicao.keySet().stream()
                .sorted() // Ordem crescente: 1, 2, 3... 10
                .forEach(posicao -> {
                    List<RepertorioBanda> musicasMesmaPosicao = musicasPorPosicao.get(posicao);
                    Collections.shuffle(musicasMesmaPosicao); // Aleatoriedade entre músicas de mesma posição
                    resultado.addAll(musicasMesmaPosicao);
                });
        
        return resultado;
    }
    
    private int calcularDuracaoTotal(List<RepertorioBanda> musicas) {
        return musicas.stream()
                .mapToInt(m -> timeToSeconds(m.getMusica().getDuracao()))
                .sum();
    }
    
    private double calcularPontuacao(RepertorioBanda musica, int posicaoAtual, int totalMusicas) {
        double pontuacao = 0.0;
        
        // Pontuação por relevância (peso 50%) - null = 5
        int relevancia = obterRelevancia(musica);
        pontuacao += relevancia * 5.0;
        
        // Pontuação por adequação à posição (peso 30%) - null = 5
        int posicaoShow = obterPosicaoShow(musica);
        double posicaoIdeal = (posicaoShow / 10.0) * totalMusicas;
        double diferenca = Math.abs(posicaoIdeal - posicaoAtual);
        double pontuacaoPosicao = Math.max(0, 30 - (diferenca * 3));
        pontuacao += pontuacaoPosicao;
        
        // Pontuação base (peso 20%)
        pontuacao += 20.0;
        
        return Math.round(pontuacao * 100.0) / 100.0;
    }
    
    /**
     * FASE 3: Ordena as músicas selecionadas para seguir a curva de energia desenhada.
     * Interpola a curva para o tamanho exato do set e usa rank-matching para atribuir
     * a música com energia mais próxima a cada posição desejada.
     */
    private List<RepertorioBanda> ordenarPorCurvaEnergia(List<RepertorioBanda> musicas, List<Integer> curva) {
        int n = musicas.size();
        List<Integer> curvaInterpolada = interpolarCurva(curva, n);

        // Posições ordenadas pela energia desejada (crescente)
        List<Integer> posicoesPorEnergiaDesejada = IntStream.range(0, n)
                .boxed()
                .sorted(Comparator.comparingInt(curvaInterpolada::get))
                .collect(Collectors.toList());

        // Músicas ordenadas pela energia real (crescente)
        List<RepertorioBanda> musicasPorEnergia = new ArrayList<>(musicas);
        musicasPorEnergia.sort(Comparator.comparingInt(this::obterEnergia));

        // Atribui: o slot com i-ésima menor energia desejada recebe a música com i-ésima menor energia real
        RepertorioBanda[] resultado = new RepertorioBanda[n];
        for (int rank = 0; rank < n; rank++) {
            resultado[posicoesPorEnergiaDesejada.get(rank)] = musicasPorEnergia.get(rank);
        }

        return new ArrayList<>(Arrays.asList(resultado));
    }

    private List<Integer> interpolarCurva(List<Integer> curva, int n) {
        if (n == 1) return Collections.singletonList(curva.get(curva.size() / 2));
        int curvaSize = curva.size();
        List<Integer> resultado = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double t = (double) i / (n - 1) * (curvaSize - 1);
            int idx = (int) t;
            double frac = t - idx;
            if (idx >= curvaSize - 1) {
                resultado.add(curva.get(curvaSize - 1));
            } else {
                int v = (int) Math.round(curva.get(idx) * (1 - frac) + curva.get(idx + 1) * frac);
                resultado.add(Math.max(1, Math.min(10, v)));
            }
        }
        return resultado;
    }

    private int timeToSeconds(Time time) {
        if (time == null) return 0;
        return time.toLocalTime().toSecondOfDay();
    }
    
    
    /**
     * Métodos auxiliares para tratar valores null como 5 (meio termo)
     */
    private int obterRelevancia(RepertorioBanda musica) {
        return musica.getRelevancia() != null ? musica.getRelevancia() : 5;
    }
    
    private int obterPosicaoShow(RepertorioBanda musica) {
        return musica.getPosicaoShow() != null ? musica.getPosicaoShow() : 5;
    }
    
    private int obterEnergia(RepertorioBanda musica) {
        return musica.getEnergia() != null ? musica.getEnergia() : 5;
    }
    
    private String gerarMensagem(int duracaoTotal, int duracaoSolicitada) {
        int diferenca = duracaoSolicitada - duracaoTotal;
        
        if (diferenca == 0) {
            return "Repertório perfeito! Duração exata do show.";
        } else if (diferenca > 0 && diferenca <= 5) {
            return String.format("Repertório ótimo! Faltam apenas %d minutos para completar o show.", diferenca);
        } else if (diferenca > 5) {
            return String.format("Repertório sugerido. Faltam %d minutos - considere adicionar mais músicas.", diferenca);
        } else {
            return String.format("Repertório sugerido com %d minutos de show.", duracaoTotal);
        }
    }
}
