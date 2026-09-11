package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Documento pronto para a casa de show. Tudo aqui e derivado do mapa -- nenhuma
 * informacao e digitada duas vezes; o campo origem de cada item e o que separa
 * as duas primeiras listas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiderTecnicoDTO {

    private Long idMapa;
    private Long idBanda;
    private String nomeBanda;
    private String urlFotoBanda;
    private String nomeMapa;
    private String descricaoMapa;
    private String dataAtualizacao;
    private String observacoes;

    /** O mapa completo, para desenhar o palco no topo do documento. */
    private MapaPalcoDTO mapa;
    private ResumoTecnicoDTO resumo;

    private List<LinhaRiderDTO> esperamosDoLocal = new ArrayList<>();
    private List<LinhaRiderDTO> bandaLeva = new ArrayList<>();
    private List<LinhaInputListDTO> inputList = new ArrayList<>();
    private List<LinhaMonitoracaoDTO> monitoracao = new ArrayList<>();
    private List<LinhaRiderDTO> energia = new ArrayList<>();
}
