package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.palco.*;
import com.baseapplication.core.enums.CategoriaItemPalco;
import com.baseapplication.core.enums.OrigemItemPalco;
import com.baseapplication.core.enums.TipoItemPalco;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Deriva resumo e rider a partir do mapa. Vive so aqui: os badges do editor e o
 * PDF leem o mesmo numero porque leem o mesmo calculo -- se isso fosse
 * duplicado no front "para ficar responsivo", as duas contas divergiriam.
 *
 * Semantica dos campos que entram na conta:
 *  - quantidade = quantas pecas fisicas (2 wedges);
 *  - vias       = quantos envios de monitor o item precisa (os 2 wedges acima
 *                 podem dividir 1 via);
 *  - canais     = quantos canais de mesa o item consome, ja editado pelo
 *                 usuario (bateria vale 8, microfone que so capta um
 *                 amplificador ja contado vale 0).
 */
@Service
public class RiderCalculoService {

    public ResumoTecnicoDTO calcularResumo(List<PosicaoPalcoDTO> posicoes, List<ItemMapaPalcoDTO> itensGerais) {
        List<ItemMapaPalcoDTO> todos = todosItens(posicoes, itensGerais);

        int totalCanais = todos.stream()
                .mapToInt(i -> nz(i.getCanais()) * Math.max(1, nz(i.getQuantidade())))
                .sum();

        List<ItemMapaPalcoDTO> monitores = todos.stream()
                .filter(i -> categoria(i) == CategoriaItemPalco.MONITORACAO)
                .collect(Collectors.toList());

        int viasRetorno = monitores.stream().mapToInt(i -> nz(i.getVias())).sum();
        int mixesIndependentes = (int) monitores.stream()
                .filter(i -> Boolean.TRUE.equals(i.getMixIndependente()))
                .count();

        ResumoTecnicoDTO resumo = new ResumoTecnicoDTO();
        resumo.setTotalPosicoes(posicoes.size());
        resumo.setPosicoesPendentes((int) posicoes.stream()
                .filter(p -> !Boolean.TRUE.equals(p.getPreenchida()))
                .count());
        resumo.setTotalCanais(totalCanais);
        resumo.setViasRetorno(viasRetorno);
        resumo.setMixesIndependentes(mixesIndependentes);
        resumo.setTomadas110(somarTomadas(todos, 110));
        resumo.setTomadas220(somarTomadas(todos, 220));
        resumo.setItensDoLocal((int) todos.stream()
                .filter(i -> i.getOrigem() == OrigemItemPalco.CASA).count());
        resumo.setItensProprios((int) todos.stream()
                .filter(i -> i.getOrigem() == OrigemItemPalco.PROPRIO).count());
        return resumo;
    }

    public RiderTecnicoDTO montarRider(MapaPalcoDTO mapa, String nomeBanda, String urlFotoBanda) {
        List<PosicaoPalcoDTO> posicoes = mapa.getPosicoes();
        List<ItemMapaPalcoDTO> itensGerais = mapa.getItensGerais();
        ResumoTecnicoDTO resumo = mapa.getResumo() != null
                ? mapa.getResumo()
                : calcularResumo(posicoes, itensGerais);

        RiderTecnicoDTO rider = new RiderTecnicoDTO();
        rider.setIdMapa(mapa.getId());
        rider.setIdBanda(mapa.getIdBanda());
        rider.setNomeBanda(nomeBanda);
        rider.setUrlFotoBanda(urlFotoBanda);
        rider.setNomeMapa(mapa.getNome());
        rider.setDescricaoMapa(mapa.getDescricao());
        rider.setDataAtualizacao(mapa.getDataAtualizacao());
        rider.setObservacoes(mapa.getObservacoes());
        rider.setPalcoMinimo(formatarPalco(mapa.getLarguraM(), mapa.getProfundidadeM()));
        rider.setMapa(mapa);
        rider.setResumo(resumo);

        rider.setEsperamosDoLocal(montarLista(posicoes, itensGerais, OrigemItemPalco.CASA, resumo));
        rider.setBandaLeva(montarLista(posicoes, itensGerais, OrigemItemPalco.PROPRIO, resumo));
        rider.setEnergia(montarEnergia(posicoes, itensGerais));
        rider.setInputList(montarInputList(posicoes));
        rider.setMonitoracao(montarMonitoracao(posicoes));
        return rider;
    }

    // ------------------------------------------------------------------
    // Listas "a banda leva" / "esperamos do local"
    // ------------------------------------------------------------------

    /**
     * Agrupa por tipo + qualificacao porque a casa de show quer o total
     * ("5 wedges"), nao uma linha por guitarrista. Quando o agrupamento vem de
     * uma unica posicao, mantem o rotulo dela; vindo de varias, fica sem dono.
     */
    private List<LinhaRiderDTO> montarLista(List<PosicaoPalcoDTO> posicoes,
                                            List<ItemMapaPalcoDTO> itensGerais,
                                            OrigemItemPalco origem,
                                            ResumoTecnicoDTO resumo) {
        Map<String, LinhaRiderDTO> agrupado = new LinkedHashMap<>();
        Map<String, Set<String>> donos = new HashMap<>();

        for (PosicaoPalcoDTO posicao : ordenadasPorCanal(posicoes)) {
            for (ItemMapaPalcoDTO item : posicao.getItens()) {
                acumular(agrupado, donos, item, origem, posicao.getRotulo());
            }
        }
        for (ItemMapaPalcoDTO item : itensGerais) {
            acumular(agrupado, donos, item, origem, null);
        }

        List<LinhaRiderDTO> linhas = new ArrayList<>();
        for (Map.Entry<String, LinhaRiderDTO> entry : agrupado.entrySet()) {
            LinhaRiderDTO linha = entry.getValue();
            Set<String> deQuem = donos.get(entry.getKey());
            linha.setPosicao(deQuem != null && deQuem.size() == 1
                    ? deQuem.iterator().next()
                    : null);
            linhas.add(linha);
        }

        // A mesa so ganha sentido com o numero de canais que o mapa exige.
        linhas.stream()
                .filter(l -> TipoItemPalco.MESA_SOM.getDescricao().equals(l.getDescricao()))
                .forEach(l -> l.setDetalhe("mínimo " + resumo.getTotalCanais() + " canais"));

        return linhas;
    }

    private void acumular(Map<String, LinhaRiderDTO> agrupado, Map<String, Set<String>> donos,
                          ItemMapaPalcoDTO item, OrigemItemPalco origem, String rotuloPosicao) {
        if (item.getOrigem() != origem || categoria(item) == CategoriaItemPalco.ENERGIA) {
            return;
        }
        String descricao = item.nomeExibicao();
        String detalhe = detalheDe(item);
        String chave = item.getTipo() + "|" + descricao + "|" + (detalhe == null ? "" : detalhe);

        LinhaRiderDTO linha = agrupado.get(chave);
        if (linha == null) {
            linha = new LinhaRiderDTO();
            linha.setCategoria(categoria(item).getDescricao());
            linha.setDescricao(descricao);
            linha.setDetalhe(detalhe);
            linha.setQuantidade(0);
            agrupado.put(chave, linha);
        }
        linha.setQuantidade(linha.getQuantidade() + Math.max(1, nz(item.getQuantidade())));

        if (rotuloPosicao != null) {
            donos.computeIfAbsent(chave, k -> new LinkedHashSet<>()).add(rotuloPosicao);
        } else {
            donos.computeIfAbsent(chave, k -> new LinkedHashSet<>());
        }
    }

    private String detalheDe(ItemMapaPalcoDTO item) {
        List<String> partes = new ArrayList<>();
        if (notBlank(item.getMarcaModelo()) && !item.nomeExibicao().contains(item.getMarcaModelo())) {
            partes.add(item.getMarcaModelo());
        }
        if (item.getVias() != null && item.getVias() > 0) {
            partes.add(item.getVias() + (item.getVias() == 1 ? " via" : " vias"));
        }
        if (notBlank(item.getObservacao())) {
            partes.add(item.getObservacao());
        }
        return partes.isEmpty() ? null : String.join(" · ", partes);
    }

    // ------------------------------------------------------------------
    // Energia
    // ------------------------------------------------------------------

    /**
     * Agrupa por voltagem + ponto, porque "3 tomadas 220V na lateral esquerda" e
     * a informacao que resolve o problema no dia -- nao o total solto.
     */
    private List<LinhaRiderDTO> montarEnergia(List<PosicaoPalcoDTO> posicoes, List<ItemMapaPalcoDTO> itensGerais) {
        Map<String, LinhaRiderDTO> agrupado = new LinkedHashMap<>();

        for (PosicaoPalcoDTO posicao : ordenadasPorCanal(posicoes)) {
            for (ItemMapaPalcoDTO item : posicao.getItens()) {
                acumularEnergia(agrupado, item, posicao.getRotulo());
            }
        }
        for (ItemMapaPalcoDTO item : itensGerais) {
            acumularEnergia(agrupado, item, null);
        }
        return new ArrayList<>(agrupado.values());
    }

    private void acumularEnergia(Map<String, LinhaRiderDTO> agrupado, ItemMapaPalcoDTO item, String rotuloPosicao) {
        if (categoria(item) != CategoriaItemPalco.ENERGIA) {
            return;
        }
        String voltagem = item.getVoltagem() != null ? item.getVoltagem() + "V" : "voltagem não informada";
        String ponto = notBlank(item.getPonto()) ? item.getPonto() : "não especificado";
        String chave = item.getTipo() + "|" + voltagem + "|" + ponto + "|" + (rotuloPosicao == null ? "" : rotuloPosicao);

        LinhaRiderDTO linha = agrupado.get(chave);
        if (linha == null) {
            linha = new LinhaRiderDTO();
            linha.setCategoria(item.getOrigem() == OrigemItemPalco.CASA ? "Do local" : "Levamos");
            linha.setDescricao(item.getTipo() == TipoItemPalco.TOMADA
                    ? "Tomada " + voltagem
                    : item.nomeExibicao());
            linha.setDetalhe(ponto);
            linha.setPosicao(rotuloPosicao);
            linha.setQuantidade(0);
            agrupado.put(chave, linha);
        }
        linha.setQuantidade(linha.getQuantidade() + Math.max(1, nz(item.getQuantidade())));
    }

    // ------------------------------------------------------------------
    // Input list
    // ------------------------------------------------------------------

    /**
     * Numera os canais na ordem convencional de mesa (bateria, percussao, baixo,
     * guitarras, teclas, sopros, vocais), que vem de ordemCanal na posicao -- o
     * admin arrasta para corrigir quando a normalizacao do instrumento erra.
     */
    private List<LinhaInputListDTO> montarInputList(List<PosicaoPalcoDTO> posicoes) {
        List<LinhaInputListDTO> linhas = new ArrayList<>();
        int canal = 1;

        for (PosicaoPalcoDTO posicao : ordenadasPorCanal(posicoes)) {
            String captacaoPadrao = captacaoDaPosicao(posicao);

            List<ItemMapaPalcoDTO> geradores = posicao.getItens().stream()
                    .filter(i -> nz(i.getCanais()) > 0)
                    .sorted(Comparator.comparing(i -> nz(i.getOrdem())))
                    .collect(Collectors.toList());

            for (ItemMapaPalcoDTO item : geradores) {
                int canaisDoItem = nz(item.getCanais()) * Math.max(1, nz(item.getQuantidade()));
                for (int n = 1; n <= canaisDoItem; n++) {
                    LinhaInputListDTO linha = new LinhaInputListDTO();
                    linha.setCanal(canal++);
                    linha.setFonte(canaisDoItem > 1
                            ? item.nomeExibicao() + " " + n
                            : item.nomeExibicao());
                    linha.setPosicao(posicao.getRotulo());
                    linha.setCaptacao(captacaoDoItem(item, captacaoPadrao));
                    linha.setOrigem(item.getOrigem() != null ? item.getOrigem().getDescricao() : null);
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    /** Um microfone ou DI com 0 canais na posicao existe para captar outra fonte. */
    private String captacaoDaPosicao(PosicaoPalcoDTO posicao) {
        for (ItemMapaPalcoDTO item : posicao.getItens()) {
            if (nz(item.getCanais()) > 0) {
                continue;
            }
            if (item.getTipo() == TipoItemPalco.MICROFONE) {
                return notBlank(item.getMarcaModelo()) ? "Microfone " + item.getMarcaModelo() : "Microfone";
            }
            if (item.getTipo() == TipoItemPalco.DI) {
                return notBlank(item.getMarcaModelo()) ? "DI " + item.getMarcaModelo() : "DI";
            }
        }
        return null;
    }

    private String captacaoDoItem(ItemMapaPalcoDTO item, String captacaoPadrao) {
        if (item.getTipo() == TipoItemPalco.MICROFONE) {
            return notBlank(item.getMarcaModelo()) ? "Microfone " + item.getMarcaModelo() : "Microfone";
        }
        if (item.getTipo() == TipoItemPalco.DI) {
            return notBlank(item.getMarcaModelo()) ? "DI " + item.getMarcaModelo() : "DI";
        }
        return captacaoPadrao != null ? captacaoPadrao : "Linha";
    }

    // ------------------------------------------------------------------
    // Monitoracao
    // ------------------------------------------------------------------

    /**
     * Mixes independentes recebem numero proprio; quem marcou mix compartilhado
     * cai todo no ultimo mix. E a leitura mais honesta do dado que existe -- o
     * mapa nao pergunta "com quem voce divide", so se a via e sua.
     */
    private List<LinhaMonitoracaoDTO> montarMonitoracao(List<PosicaoPalcoDTO> posicoes) {
        List<LinhaMonitoracaoDTO> independentes = new ArrayList<>();
        List<LinhaMonitoracaoDTO> compartilhados = new ArrayList<>();

        for (PosicaoPalcoDTO posicao : ordenadasPorCanal(posicoes)) {
            for (ItemMapaPalcoDTO item : posicao.getItens()) {
                if (categoria(item) != CategoriaItemPalco.MONITORACAO) {
                    continue;
                }
                LinhaMonitoracaoDTO linha = new LinhaMonitoracaoDTO();
                linha.setPosicao(posicao.getRotulo());
                linha.setTipo(item.getTipoDescricao());
                linha.setVias(nz(item.getVias()));
                linha.setOrigem(item.getOrigem() != null ? item.getOrigem().getDescricao() : null);
                linha.setIndependente(Boolean.TRUE.equals(item.getMixIndependente()));

                if (linha.getIndependente()) {
                    independentes.add(linha);
                } else {
                    compartilhados.add(linha);
                }
            }
        }

        int mix = 1;
        for (LinhaMonitoracaoDTO linha : independentes) {
            linha.setMix(mix++);
        }
        for (LinhaMonitoracaoDTO linha : compartilhados) {
            linha.setMix(mix);
        }

        List<LinhaMonitoracaoDTO> todas = new ArrayList<>(independentes);
        todas.addAll(compartilhados);
        return todas;
    }

    // ------------------------------------------------------------------

    private List<PosicaoPalcoDTO> ordenadasPorCanal(List<PosicaoPalcoDTO> posicoes) {
        return posicoes.stream()
                // Desempate por linha e depois da esquerda para a direita, que e
                // a ordem em que o tecnico varre o palco.
                .sorted(Comparator.comparing((PosicaoPalcoDTO p) -> nz(p.getOrdemCanal()))
                        .thenComparing(p -> nz(p.getLinha()))
                        .thenComparing(p -> p.getPosX() == null ? 0d : p.getPosX()))
                .collect(Collectors.toList());
    }

    private List<ItemMapaPalcoDTO> todosItens(List<PosicaoPalcoDTO> posicoes, List<ItemMapaPalcoDTO> itensGerais) {
        return Stream.concat(
                posicoes.stream().flatMap(p -> p.getItens().stream()),
                itensGerais.stream()
        ).collect(Collectors.toList());
    }

    private int somarTomadas(List<ItemMapaPalcoDTO> itens, int voltagem) {
        return itens.stream()
                .filter(i -> i.getTipo() == TipoItemPalco.TOMADA)
                .filter(i -> i.getVoltagem() != null && i.getVoltagem() == voltagem)
                .mapToInt(i -> Math.max(1, nz(i.getQuantidade())))
                .sum();
    }

    private CategoriaItemPalco categoria(ItemMapaPalcoDTO item) {
        return item.getTipo() != null ? item.getTipo().getCategoria() : CategoriaItemPalco.ESTRUTURA;
    }

    /** O desenho e em escala, entao a dimensao do palco e um dado do rider. */
    private String formatarPalco(Double largura, Double profundidade) {
        if (largura == null || profundidade == null) {
            return null;
        }
        return String.format(new Locale("pt", "BR"), "%.1f × %.1f m", largura, profundidade);
    }

    private int nz(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private boolean notBlank(String valor) {
        return valor != null && !valor.isBlank();
    }
}
