package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.*;
import com.baseapplication.core.dto.palco.*;
import com.baseapplication.core.enums.OrigemItemPalco;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.TipoItemPalco;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.*;
import com.baseapplication.core.service.MapaPalcoService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapaPalcoServiceImpl implements MapaPalcoService {

    private final MapaPalcoDao mapaPalcoDao;
    private final PosicaoPalcoDao posicaoPalcoDao;
    private final ItemMapaPalcoDao itemMapaPalcoDao;
    private final MusicoBandaDao musicoBandaDao;
    private final BandaDao bandaDao;
    private final MembroFantasmaDao membroFantasmaDao;
    private final ShowDao showDao;
    private final TemplateMapaPalcoService templateService;
    private final RiderCalculoService riderCalculoService;

    // ------------------------------------------------------------------
    // Leitura
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<MapaPalcoResumoDTO> listarPorBanda(Long idBanda) {
        exigirMembro(idBanda);

        List<MapaPalco> mapas = mapaPalcoDao.buscarPorIdBanda(idBanda);
        if (mapas.isEmpty()) {
            return Collections.emptyList();
        }

        // Uma consulta so para as posicoes de todos os mapas da lista, em vez de
        // uma por card.
        List<Long> ids = mapas.stream().map(MapaPalco::getId).collect(Collectors.toList());
        Map<Long, List<PosicaoPalco>> posicoesPorMapa = posicaoPalcoDao.buscarPorIdsMapaPalco(ids).stream()
                .collect(Collectors.groupingBy(PosicaoPalco::getIdMapaPalco));

        return mapas.stream().map(mapa -> {
            MapaPalcoResumoDTO dto = new MapaPalcoResumoDTO(mapa);
            List<PosicaoPalco> posicoes = posicoesPorMapa.getOrDefault(mapa.getId(), Collections.emptyList());
            dto.setTotalPosicoes(posicoes.size());
            dto.setPosicoesPendentes((int) posicoes.stream()
                    .filter(p -> p.getDataPreenchimento() == null)
                    .count());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MapaPalcoDTO buscarAgregado(Long id) {
        MapaPalco mapa = buscarMapa(id);
        exigirMembro(mapa.getIdBanda());
        return montarAgregado(mapa);
    }

    // ------------------------------------------------------------------
    // Escrita
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public MapaPalcoDTO criar(Long idBanda, CadastroMapaPalcoDTO dto) {
        exigirPermissaoDeEdicao(idBanda);

        TemplateMapaPalcoDTO template = templateService.existe(dto.getTemplate())
                ? templateService.buscar(dto.getTemplate())
                : null;

        MapaPalco mapa = new MapaPalco();
        mapa.setIdBanda(idBanda);
        mapa.setNome(primeiroNaoVazio(dto.getNome(), template != null ? template.getNome() : "Novo mapa de palco"));
        mapa.setDescricao(primeiroNaoVazio(dto.getDescricao(), template != null ? template.getDescricao() : null));
        mapa.setLarguraM(dec(template != null ? template.getLarguraM() : null, "8.00"));
        mapa.setProfundidadeM(dec(template != null ? template.getProfundidadeM() : null, "6.00"));
        mapa.setGradeLinhas(template != null && template.getGradeLinhas() != null ? template.getGradeLinhas() : 4);
        // O primeiro mapa da banda ja nasce como o padrao exibido no perfil.
        mapa.setPadrao(mapaPalcoDao.contarPorIdBanda(idBanda) == 0);
        mapa.setAtivo(true);
        mapa.setDataCriacao(LocalDateTime.now());
        mapa.setDataAtualizacao(LocalDateTime.now());
        mapa = mapaPalcoDao.save(mapa);

        if (template != null) {
            aplicarTemplate(mapa, template);
        }

        return montarAgregado(mapa);
    }

    private void aplicarTemplate(MapaPalco mapa, TemplateMapaPalcoDTO template) {
        for (TemplateMapaPalcoDTO.PosicaoTemplateDTO posicaoTemplate : template.getPosicoes()) {
            PosicaoPalco posicao = new PosicaoPalco();
            posicao.setIdMapaPalco(mapa.getId());
            posicao.setRotulo(posicaoTemplate.getRotulo());
            posicao.setInstrumento(posicaoTemplate.getInstrumento());
            posicao.setPosX(dec(posicaoTemplate.getPosX(), "50.00"));
            posicao.setLinha(nz(posicaoTemplate.getLinha()));
            posicao.setEscala(dec(posicaoTemplate.getEscala(), "1.00"));
            posicao.setBackingVocal(Boolean.TRUE.equals(posicaoTemplate.getBackingVocal()));
            posicao.setOrdemCanal(nz(posicaoTemplate.getOrdemCanal()));
            // Vem do template sem dono: nasce pendente de propositalmente, para o
            // badge cobrar que cada membro confira o proprio equipamento.
            posicao.setDataPreenchimento(null);
            posicao = posicaoPalcoDao.save(posicao);

            salvarItens(mapa.getId(), posicao.getId(), posicaoTemplate.getItens());
        }
        salvarItens(mapa.getId(), null, template.getItensGerais());
    }

    @Override
    @Transactional
    public MapaPalcoDTO atualizar(Long id, AtualizacaoMapaPalcoDTO dto) {
        MapaPalco mapa = buscarMapa(id);
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        if (dto.getNome() != null) {
            mapa.setNome(dto.getNome());
        }
        if (dto.getDescricao() != null) {
            mapa.setDescricao(dto.getDescricao());
        }
        if (dto.getObservacoes() != null) {
            mapa.setObservacoes(dto.getObservacoes());
        }
        if (dto.getLarguraM() != null) {
            mapa.setLarguraM(BigDecimal.valueOf(dto.getLarguraM()));
        }
        if (dto.getProfundidadeM() != null) {
            mapa.setProfundidadeM(BigDecimal.valueOf(dto.getProfundidadeM()));
        }
        if (dto.getGradeLinhas() != null) {
            mapa.setGradeLinhas(dto.getGradeLinhas());
        }
        mapa.setDataAtualizacao(LocalDateTime.now());
        mapaPalcoDao.save(mapa);

        // Layout em lote: arrastar tres blocos com autosave vira uma chamada so.
        if (dto.getPosicoes() != null && !dto.getPosicoes().isEmpty()) {
            Map<Long, PosicaoPalco> existentes = posicaoPalcoDao.buscarPorIdMapaPalco(id).stream()
                    .collect(Collectors.toMap(PosicaoPalco::getId, p -> p));

            for (AtualizacaoMapaPalcoDTO.LayoutPosicaoDTO layout : dto.getPosicoes()) {
                PosicaoPalco posicao = existentes.get(layout.getId());
                if (posicao == null) {
                    continue;
                }
                if (layout.getPosX() != null) {
                    posicao.setPosX(BigDecimal.valueOf(limitarPercentual(layout.getPosX())));
                }
                if (layout.getLinha() != null) {
                    posicao.setLinha(Math.max(0, layout.getLinha()));
                }
                if (layout.getEscala() != null) {
                    posicao.setEscala(BigDecimal.valueOf(layout.getEscala()));
                }
                if (layout.getOrdemCanal() != null) {
                    posicao.setOrdemCanal(layout.getOrdemCanal());
                }
                posicaoPalcoDao.save(posicao);
            }
        }

        // Ajuste manual de pecas soltas. Item nao enviado mantem pos_x/pos_y
        // nulos e continua sendo posicionado automaticamente pelo dono.
        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            Map<Long, ItemMapaPalco> itensExistentes = itemMapaPalcoDao.buscarPorIdMapaPalco(id).stream()
                    .collect(Collectors.toMap(ItemMapaPalco::getId, i -> i));

            for (AtualizacaoMapaPalcoDTO.LayoutItemDTO layout : dto.getItens()) {
                ItemMapaPalco item = itensExistentes.get(layout.getId());
                if (item == null) {
                    continue;
                }
                if (layout.getPosX() != null) {
                    item.setPosX(BigDecimal.valueOf(limitarPercentual(layout.getPosX())));
                }
                if (layout.getPosY() != null) {
                    item.setPosY(BigDecimal.valueOf(limitarPercentual(layout.getPosY())));
                }
                if (layout.getEscala() != null) {
                    item.setEscala(BigDecimal.valueOf(layout.getEscala()));
                }
                itemMapaPalcoDao.save(item);
            }
        }

        if (dto.getItensGerais() != null) {
            itemMapaPalcoDao.deletarGeraisPorIdMapaPalco(id);
            salvarItens(id, null, dto.getItensGerais());
        }

        return montarAgregado(mapa);
    }

    @Override
    @Transactional
    public MapaPalcoDTO duplicar(Long id, String novoNome) {
        MapaPalco origem = buscarMapa(id);
        exigirPermissaoDeEdicao(origem.getIdBanda());

        MapaPalco copia = new MapaPalco();
        copia.setIdBanda(origem.getIdBanda());
        copia.setNome(primeiroNaoVazio(novoNome, "Cópia de " + origem.getNome()));
        copia.setDescricao(origem.getDescricao());
        copia.setObservacoes(origem.getObservacoes());
        copia.setLarguraM(origem.getLarguraM());
        copia.setProfundidadeM(origem.getProfundidadeM());
        copia.setGradeLinhas(origem.getGradeLinhas());
        copia.setIdDerivadoDe(origem.getId());
        copia.setPadrao(false);
        copia.setAtivo(true);
        copia.setDataCriacao(LocalDateTime.now());
        copia.setDataAtualizacao(LocalDateTime.now());
        copia = mapaPalcoDao.save(copia);

        List<ItemMapaPalco> itensOrigem = itemMapaPalcoDao.buscarPorIdMapaPalco(id);
        Map<Long, List<ItemMapaPalco>> itensPorPosicao = itensOrigem.stream()
                .filter(i -> i.getIdPosicao() != null)
                .collect(Collectors.groupingBy(ItemMapaPalco::getIdPosicao));

        for (PosicaoPalco posicaoOrigem : posicaoPalcoDao.buscarPorIdMapaPalco(id)) {
            PosicaoPalco posicaoCopia = new PosicaoPalco();
            posicaoCopia.setIdMapaPalco(copia.getId());
            posicaoCopia.setRotulo(posicaoOrigem.getRotulo());
            posicaoCopia.setInstrumento(posicaoOrigem.getInstrumento());
            posicaoCopia.setPosX(posicaoOrigem.getPosX());
            posicaoCopia.setLinha(posicaoOrigem.getLinha());
            posicaoCopia.setEscala(posicaoOrigem.getEscala());
            posicaoCopia.setIdUsuario(posicaoOrigem.getIdUsuario());
            posicaoCopia.setIdMembroFantasma(posicaoOrigem.getIdMembroFantasma());
            posicaoCopia.setBackingVocal(posicaoOrigem.getBackingVocal());
            posicaoCopia.setOrdemCanal(posicaoOrigem.getOrdemCanal());
            posicaoCopia.setObservacao(posicaoOrigem.getObservacao());
            // A copia herda o estado de preenchimento: duplicar um mapa pronto
            // nao deve marcar tudo como pendente de novo.
            posicaoCopia.setDataPreenchimento(posicaoOrigem.getDataPreenchimento());
            posicaoCopia = posicaoPalcoDao.save(posicaoCopia);

            for (ItemMapaPalco item : itensPorPosicao.getOrDefault(posicaoOrigem.getId(), Collections.emptyList())) {
                copiarItem(item, copia.getId(), posicaoCopia.getId());
            }
        }

        for (ItemMapaPalco item : itensOrigem) {
            if (item.getIdPosicao() == null) {
                copiarItem(item, copia.getId(), null);
            }
        }

        return montarAgregado(copia);
    }

    private void copiarItem(ItemMapaPalco origem, Long idMapa, Long idPosicao) {
        ItemMapaPalco copia = new ItemMapaPalco();
        copia.setIdMapaPalco(idMapa);
        copia.setIdPosicao(idPosicao);
        copia.setTipo(origem.getTipo());
        copia.setOrigem(origem.getOrigem());
        copia.setQuantidade(origem.getQuantidade());
        copia.setRotulo(origem.getRotulo());
        copia.setMarcaModelo(origem.getMarcaModelo());
        copia.setObservacao(origem.getObservacao());
        copia.setOrdem(origem.getOrdem());
        copia.setCanais(origem.getCanais());
        copia.setVoltagem(origem.getVoltagem());
        copia.setVias(origem.getVias());
        copia.setMixIndependente(origem.getMixIndependente());
        copia.setPonto(origem.getPonto());
        copia.setPosX(origem.getPosX());
        copia.setPosY(origem.getPosY());
        copia.setEscala(origem.getEscala());
        itemMapaPalcoDao.save(copia);
    }

    @Override
    @Transactional
    public MapaPalcoDTO definirPadrao(Long id) {
        MapaPalco mapa = buscarMapa(id);
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        mapaPalcoDao.limparPadraoDaBanda(mapa.getIdBanda());
        mapa.setPadrao(true);
        mapa.setDataAtualizacao(LocalDateTime.now());
        mapaPalcoDao.save(mapa);
        return montarAgregado(mapa);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        MapaPalco mapa = buscarMapa(id);
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        mapa.setAtivo(false);
        // Sai do indice de padrao unico junto com a desativacao, senao a banda
        // fica sem conseguir eleger um novo padrao.
        mapa.setPadrao(false);
        mapa.setDataAtualizacao(LocalDateTime.now());
        mapaPalcoDao.save(mapa);
    }

    // ------------------------------------------------------------------
    // Posicoes
    // ------------------------------------------------------------------

    @Override
    @Transactional
    public PosicaoPalcoDTO criarPosicao(Long idMapa, CadastroPosicaoPalcoDTO dto) {
        MapaPalco mapa = buscarMapa(idMapa);
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        PosicaoPalco posicao = new PosicaoPalco();
        posicao.setIdMapaPalco(idMapa);
        aplicarDadosDaPosicao(posicao, dto);
        posicao = posicaoPalcoDao.save(posicao);

        tocarMapa(mapa);
        return montarPosicao(posicao, Collections.emptyList());
    }

    @Override
    @Transactional
    public PosicaoPalcoDTO atualizarPosicao(Long idPosicao, CadastroPosicaoPalcoDTO dto) {
        PosicaoPalco posicao = buscarPosicao(idPosicao);
        MapaPalco mapa = buscarMapa(posicao.getIdMapaPalco());
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        aplicarDadosDaPosicao(posicao, dto);
        posicao = posicaoPalcoDao.save(posicao);

        tocarMapa(mapa);
        return montarPosicao(posicao, itemMapaPalcoDao.buscarPorIdPosicao(idPosicao));
    }

    private void aplicarDadosDaPosicao(PosicaoPalco posicao, CadastroPosicaoPalcoDTO dto) {
        if (dto.getRotulo() != null) {
            posicao.setRotulo(dto.getRotulo());
        }
        if (posicao.getRotulo() == null || posicao.getRotulo().isBlank()) {
            posicao.setRotulo("Nova posição");
        }
        if (dto.getInstrumento() != null) {
            posicao.setInstrumento(dto.getInstrumento());
        }
        if (dto.getPosX() != null) {
            posicao.setPosX(BigDecimal.valueOf(limitarPercentual(dto.getPosX())));
        }
        if (dto.getLinha() != null) {
            posicao.setLinha(Math.max(0, dto.getLinha()));
        }
        if (dto.getEscala() != null) {
            posicao.setEscala(BigDecimal.valueOf(dto.getEscala()));
        }
        if (dto.getBackingVocal() != null) {
            posicao.setBackingVocal(dto.getBackingVocal());
        }
        if (dto.getOrdemCanal() != null) {
            posicao.setOrdemCanal(dto.getOrdemCanal());
        }
        if (dto.getObservacao() != null) {
            posicao.setObservacao(dto.getObservacao());
        }

        // Ocupante e exclusivo: vincular um membro limpa o fantasma e vice-versa,
        // como o CHECK da tabela exige.
        if (dto.getIdUsuario() != null) {
            posicao.setIdUsuario(dto.getIdUsuario() == 0 ? null : dto.getIdUsuario());
            if (posicao.getIdUsuario() != null) {
                posicao.setIdMembroFantasma(null);
            }
        }
        if (dto.getIdMembroFantasma() != null) {
            posicao.setIdMembroFantasma(dto.getIdMembroFantasma() == 0 ? null : dto.getIdMembroFantasma());
            if (posicao.getIdMembroFantasma() != null) {
                posicao.setIdUsuario(null);
            }
        }
    }

    @Override
    @Transactional
    public void deletarPosicao(Long idPosicao) {
        PosicaoPalco posicao = buscarPosicao(idPosicao);
        MapaPalco mapa = buscarMapa(posicao.getIdMapaPalco());
        exigirPermissaoDeEdicao(mapa.getIdBanda());

        itemMapaPalcoDao.deletarPorIdPosicao(idPosicao);
        posicaoPalcoDao.delete(posicao);
        tocarMapa(mapa);
    }

    @Override
    @Transactional
    public MapaPalcoDTO salvarFicha(Long idPosicao, FichaPosicaoDTO dto) {
        PosicaoPalco posicao = buscarPosicao(idPosicao);
        MapaPalco mapa = buscarMapa(posicao.getIdMapaPalco());
        exigirPermissaoDeFicha(mapa.getIdBanda(), posicao);

        if (dto.getObservacao() != null) {
            posicao.setObservacao(dto.getObservacao());
        }
        posicao.setDataPreenchimento(LocalDateTime.now());
        posicaoPalcoDao.save(posicao);

        // A lista enviada substitui a anterior inteira: o front manda a ficha
        // como um todo e nao precisa sincronizar item a item.
        itemMapaPalcoDao.deletarPorIdPosicao(idPosicao);
        salvarItens(mapa.getId(), idPosicao, dto.getItens());

        tocarMapa(mapa);
        return montarAgregado(mapa);
    }

    private void salvarItens(Long idMapa, Long idPosicao, List<CadastroItemMapaPalcoDTO> itens) {
        if (itens == null || itens.isEmpty()) {
            return;
        }
        int ordem = 0;
        for (CadastroItemMapaPalcoDTO dto : itens) {
            if (dto.getTipo() == null) {
                continue;
            }
            ItemMapaPalco item = new ItemMapaPalco();
            item.setIdMapaPalco(idMapa);
            item.setIdPosicao(idPosicao);
            item.setTipo(dto.getTipo());
            item.setOrigem(dto.getOrigem() != null ? dto.getOrigem() : OrigemItemPalco.PROPRIO);
            item.setQuantidade(Math.max(1, nz(dto.getQuantidade())));
            item.setRotulo(dto.getRotulo());
            item.setMarcaModelo(dto.getMarcaModelo());
            item.setObservacao(dto.getObservacao());
            item.setOrdem(dto.getOrdem() != null ? dto.getOrdem() : ordem);
            // Sem valor explicito, herda a sugestao do tipo -- e o que faz o
            // template de bateria ja chegar valendo 8 canais.
            item.setCanais(dto.getCanais() != null ? dto.getCanais() : dto.getTipo().getCanaisPadrao());
            item.setVoltagem(dto.getVoltagem());
            item.setVias(dto.getVias());
            item.setMixIndependente(dto.getMixIndependente());
            item.setPonto(dto.getPonto());
            item.setPosX(dto.getPosX() != null ? BigDecimal.valueOf(limitarPercentual(dto.getPosX())) : null);
            item.setPosY(dto.getPosY() != null ? BigDecimal.valueOf(limitarPercentual(dto.getPosY())) : null);
            item.setEscala(dto.getEscala() != null ? BigDecimal.valueOf(dto.getEscala()) : null);
            itemMapaPalcoDao.save(item);
            ordem++;
        }
    }

    // ------------------------------------------------------------------
    // Rider e catalogos
    // ------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public RiderTecnicoDTO gerarRider(Long id) {
        MapaPalco mapa = buscarMapa(id);
        exigirMembro(mapa.getIdBanda());
        return montarRider(mapa);
    }

    @Override
    @Transactional(readOnly = true)
    public RiderTecnicoDTO gerarRiderPublico(Long idBanda, Long idMapa) {
        MapaPalco mapa = buscarMapa(idMapa);
        if (!Objects.equals(mapa.getIdBanda(), idBanda)) {
            throw new ResourceNotFoundException("Mapa de palco não encontrado para esta banda");
        }
        return montarRider(mapa);
    }

    @Override
    @Transactional(readOnly = true)
    public RiderTecnicoDTO gerarRiderPadrao(Long idBanda) {
        // Null (204) em vez de 404: "esta banda não tem mapa" é uma resposta
        // normal para o perfil público, não um erro a ser logado.
        return mapaPalcoDao.buscarPadraoDaBanda(idBanda)
                .map(this::montarRider)
                .orElse(null);
    }

    private RiderTecnicoDTO montarRider(MapaPalco mapa) {
        MapaPalcoDTO agregado = montarAgregado(mapa);
        Banda banda = bandaDao.findById(mapa.getIdBanda()).orElse(null);
        return riderCalculoService.montarRider(
                agregado,
                banda != null ? banda.getNome() : null,
                banda != null ? banda.getUrlLogo() : null);
    }

    @Override
    @Transactional
    public void vincularAoShow(Long idShow, Long idMapa) {
        Show show = showDao.findById(idShow)
                .orElseThrow(() -> new ResourceNotFoundException("Show não encontrado"));
        Long idBanda = show.getBanda() != null ? show.getBanda().getId() : null;
        exigirPermissaoDeEdicao(idBanda);

        if (idMapa == null) {
            show.setIdMapaPalco(null);
        } else {
            MapaPalco mapa = buscarMapa(idMapa);
            // Impede apontar o show para o mapa de outra banda.
            if (!Objects.equals(mapa.getIdBanda(), idBanda)) {
                throw new RestrictionException("Este mapa de palco não pertence à banda do show");
            }
            show.setIdMapaPalco(idMapa);
        }
        showDao.save(show);
    }

    @Override
    public List<TemplateMapaPalcoDTO> listarTemplates() {
        return templateService.listar();
    }

    @Override
    public List<TipoItemPalcoDTO> listarTiposItem() {
        return TipoItemPalco.getAll().stream()
                .map(TipoItemPalcoDTO::new)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // Montagem do agregado
    // ------------------------------------------------------------------

    /**
     * Monta o agregado explicitamente a partir dos DAOs. E o unico GET que o
     * editor precisa, e nao depende de colecao lazy nenhuma -- se responde, a
     * tela funciona.
     */
    private MapaPalcoDTO montarAgregado(MapaPalco mapa) {
        MapaPalcoDTO dto = new MapaPalcoDTO(mapa);

        List<PosicaoPalco> posicoes = posicaoPalcoDao.buscarPorIdMapaPalco(mapa.getId());
        Map<Long, List<ItemMapaPalco>> itensPorPosicao = new HashMap<>();
        List<ItemMapaPalcoDTO> itensGerais = new ArrayList<>();

        for (ItemMapaPalco item : itemMapaPalcoDao.buscarPorIdMapaPalco(mapa.getId())) {
            if (item.getIdPosicao() == null) {
                itensGerais.add(new ItemMapaPalcoDTO(item));
            } else {
                itensPorPosicao.computeIfAbsent(item.getIdPosicao(), k -> new ArrayList<>()).add(item);
            }
        }

        List<PosicaoPalcoDTO> posicoesDTO = posicoes.stream()
                .map(p -> montarPosicao(p, itensPorPosicao.getOrDefault(p.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        dto.setPosicoes(posicoesDTO);
        dto.setItensGerais(itensGerais);
        dto.setResumo(riderCalculoService.calcularResumo(posicoesDTO, itensGerais));
        return dto;
    }

    private PosicaoPalcoDTO montarPosicao(PosicaoPalco posicao, List<ItemMapaPalco> itens) {
        PosicaoPalcoDTO dto = new PosicaoPalcoDTO(posicao);
        dto.setItens(itens.stream().map(ItemMapaPalcoDTO::new).collect(Collectors.toList()));

        if (posicao.getIdUsuario() != null && posicao.getUsuario() != null) {
            dto.setNomeOcupante(posicao.getUsuario().getNome());
            dto.setUrlFotoOcupante(posicao.getUsuario().getUrlFotoPerfil());
        } else if (posicao.getIdMembroFantasma() != null) {
            membroFantasmaDao.findById(posicao.getIdMembroFantasma()).ifPresent(fantasma -> {
                dto.setNomeOcupante(fantasma.getNome());
                dto.setUrlFotoOcupante(fantasma.getUrlFoto());
            });
        }
        return dto;
    }

    // ------------------------------------------------------------------
    // Permissoes
    // ------------------------------------------------------------------

    private MusicoBanda exigirMembro(Long idBanda) {
        Usuario logado = Context.getUsuarioLogado();
        if (logado == null || logado.getId() == null) {
            throw new RestrictionException("Usuário não autenticado");
        }
        return musicoBandaDao.buscarMembro(idBanda, logado.getId())
                .orElseThrow(() -> new RestrictionException("Você não faz parte desta banda"));
    }

    /** Layout, posicoes e itens gerais sao do admin do mapa. */
    private void exigirPermissaoDeEdicao(Long idBanda) {
        MusicoBanda membro = exigirMembro(idBanda);
        if (!podeGerenciar(membro)) {
            throw new RestrictionException("Você não tem permissão para editar o mapa de palco desta banda");
        }
    }

    /**
     * A ficha tecnica e do dono da posicao: quem sabe quantos volts a pedaleira
     * puxa e quem carrega a pedaleira. O admin tambem pode, para revisar.
     */
    private void exigirPermissaoDeFicha(Long idBanda, PosicaoPalco posicao) {
        MusicoBanda membro = exigirMembro(idBanda);
        boolean ehDono = Objects.equals(posicao.getIdUsuario(), membro.getId().getIdUsuario());
        if (!podeGerenciar(membro) && !ehDono) {
            throw new RestrictionException("Você só pode preencher a ficha da sua própria posição");
        }
    }

    private boolean podeGerenciar(MusicoBanda membro) {
        List<PermissaoMusico> permissoes = membro.getPermissoes();
        return permissoes != null && (permissoes.contains(PermissaoMusico.FUNDADOR)
                || permissoes.contains(PermissaoMusico.ADMINISTRADOR)
                || permissoes.contains(PermissaoMusico.GERENCIA_MAPA_PALCO));
    }

    // ------------------------------------------------------------------

    private MapaPalco buscarMapa(Long id) {
        MapaPalco mapa = mapaPalcoDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapa de palco não encontrado"));
        if (!Boolean.TRUE.equals(mapa.getAtivo())) {
            throw new ResourceNotFoundException("Mapa de palco não encontrado");
        }
        return mapa;
    }

    private PosicaoPalco buscarPosicao(Long id) {
        return posicaoPalcoDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Posição não encontrada"));
    }

    private void tocarMapa(MapaPalco mapa) {
        mapa.setDataAtualizacao(LocalDateTime.now());
        mapaPalcoDao.save(mapa);
    }

    private String primeiroNaoVazio(String preferido, String alternativo) {
        return preferido != null && !preferido.isBlank() ? preferido : alternativo;
    }

    private int nz(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private BigDecimal dec(Double valor, String padrao) {
        return valor != null ? BigDecimal.valueOf(valor) : new BigDecimal(padrao);
    }

    /** Mantem a peca dentro do palco mesmo se o front mandar algo fora de faixa. */
    private double limitarPercentual(double valor) {
        return Math.max(0d, Math.min(100d, valor));
    }
}
