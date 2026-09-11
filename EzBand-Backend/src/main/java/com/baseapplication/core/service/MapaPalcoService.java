package com.baseapplication.core.service;

import com.baseapplication.core.dto.palco.*;

import java.util.List;

public interface MapaPalcoService {

    List<MapaPalcoResumoDTO> listarPorBanda(Long idBanda);

    /** Agregado completo: mapa, posicoes com fichas, itens gerais e resumo calculado. */
    MapaPalcoDTO buscarAgregado(Long id);

    MapaPalcoDTO criar(Long idBanda, CadastroMapaPalcoDTO dto);

    MapaPalcoDTO atualizar(Long id, AtualizacaoMapaPalcoDTO dto);

    MapaPalcoDTO duplicar(Long id, String novoNome);

    MapaPalcoDTO definirPadrao(Long id);

    void deletar(Long id);

    PosicaoPalcoDTO criarPosicao(Long idMapa, CadastroPosicaoPalcoDTO dto);

    PosicaoPalcoDTO atualizarPosicao(Long idPosicao, CadastroPosicaoPalcoDTO dto);

    void deletarPosicao(Long idPosicao);

    /** Substitui a ficha inteira da posicao e devolve o agregado com o resumo novo. */
    MapaPalcoDTO salvarFicha(Long idPosicao, FichaPosicaoDTO dto);

    RiderTecnicoDTO gerarRider(Long id);

    /** Versao publica: nao exige autenticacao, valida que o mapa pertence a banda. */
    RiderTecnicoDTO gerarRiderPublico(Long idBanda, Long idMapa);

    /**
     * Rider do mapa marcado como padrao. E o que o perfil publico da banda usa
     * para decidir se mostra o botao -- devolve null quando a banda nao tem mapa.
     */
    RiderTecnicoDTO gerarRiderPadrao(Long idBanda);

    /** Vincula (ou desvincula, com idMapa nulo) um mapa a um show. */
    void vincularAoShow(Long idShow, Long idMapa);

    List<TemplateMapaPalcoDTO> listarTemplates();

    List<TipoItemPalcoDTO> listarTiposItem();
}
