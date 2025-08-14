package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.OrcamentoDao;
import com.baseapplication.core.dto.NovoOrcamentoDTO;
import com.baseapplication.core.dto.ParametroOrcamento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.ParametroCustoDTO;
import com.baseapplication.core.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrcamentoServiceImpl implements OrcamentoService {

    private final OrcamentoDao dao;
    private final ParametroCustoService parametroCustoService;
    private final BandaService bandaService;
    private final EventoService eventoService;
    private final CondicaoOrcamentoService condicaoOrcamentoService;

    @Override
    public List<ParametroCustoDTO> buscarParametrosCusto(Long idBanda) {
        return parametroCustoService.buscarPorIdBanda(idBanda).stream().map(ParametroCustoDTO::new).toList();
    }

    @Override
    public void salvarParametrosCusto(List<ParametroCustoDTO> dtos) {
        for (ParametroCustoDTO dto : dtos) {
            parametroCustoService.salvar(dto.toEntity());
        }
    }

    @Override
    public void deletarParametroCusto(ParametroCustoDTO dto) {
        ParametroCusto parametroCusto = parametroCustoService.buscarPorId(dto.getId());
        parametroCustoService.deletar(parametroCusto);
    }

    @Override
    public void gerarOrcamento(NovoOrcamentoDTO novoOrcamento) {
        Orcamento orcamento = new Orcamento();

        Banda banda = buscarBandaParaOrcamento(novoOrcamento);
        Show show = buscarShowParaOrcamento(novoOrcamento);
        List<ItemOrcamento> itensOrcamento = montarItensOrcamento(novoOrcamento, orcamento);

        orcamento.setCondicoes(filtrarCondicoesSelecionadas(novoOrcamento, banda));
        orcamento.setBanda(banda);
        orcamento.setEvento(show);
        orcamento.setValorTotal(calcularTotalOrcamento(orcamento, itensOrcamento));
        orcamento.setDataGeracao(LocalDateTime.now());
        dao.save(orcamento);
    }

    private static List<CondicaoOrcamento> filtrarCondicoesSelecionadas(NovoOrcamentoDTO novoOrcamento, Banda banda) {
        return banda.getCondicoesOrcamento().stream()
                .filter(condicao -> novoOrcamento.getIdsCondicoes().contains(condicao.getId()))
                .toList();
    }

    private static BigDecimal calcularTotalOrcamento(Orcamento orcamento, List<ItemOrcamento> itensOrcamento) {
        orcamento.setItens(itensOrcamento);
        BigDecimal total = itensOrcamento.stream()
                .map(ItemOrcamento::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    private List<ItemOrcamento> montarItensOrcamento(NovoOrcamentoDTO novoOrcamento, Orcamento orcamento) {
        List<ItemOrcamento> itensOrcamento = new ArrayList<>();
        for (ParametroOrcamento parametro : novoOrcamento.getParametros()) {
            ItemOrcamento itemOrcamento = new ItemOrcamento();
            ParametroCusto parametroCusto = parametroCustoService.buscarPorNome(parametro.getNome());
            if (parametroCusto == null) {
                throw new InternalException("Parâmetro " + parametro.getNome() + " não encontrado.");
            }
            itemOrcamento.setOrcamento(orcamento);
            itemOrcamento.setUnidade(parametroCusto.getUnidade());
            itemOrcamento.setQuantidade(parametro.getQuantidade());
            itemOrcamento.setValorCustomizado(parametro.getValorCustomizado());
            itemOrcamento.setValorUnitario(parametroCusto.getValorUnitario());
            itemOrcamento.setSubtotal(parametro.getValorCustomizado() != null ?
                    parametro.getValorCustomizado().multiply(parametro.getQuantidade()) :
                    parametroCusto.getValorUnitario().multiply(parametro.getQuantidade())
            );
            itensOrcamento.add(itemOrcamento);
        }
        return itensOrcamento;
    }

    private Show buscarShowParaOrcamento(NovoOrcamentoDTO novoOrcamento) {
        Show show = (Show) eventoService.buscarEvento(novoOrcamento.getIdEvento(), TipoEvento.SHOW);
        if(show == null){
            throw new InternalException("Evento não encontrado");
        }
        return show;
    }

    private Banda buscarBandaParaOrcamento(NovoOrcamentoDTO novoOrcamento) {
        Banda banda = bandaService.buscarPorId(novoOrcamento.getIdBanda());
        if(banda == null){
            throw new InternalException("Banda não encontrada");
        }
        return banda;
    }


}
