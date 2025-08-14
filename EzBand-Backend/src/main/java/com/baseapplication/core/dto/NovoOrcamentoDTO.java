package com.baseapplication.core.dto;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.ItemOrcamento;
import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NovoOrcamentoDTO {

    private Long idEvento;
    private Long idBanda;
    private EnderecoDTO enderecoDestinoCompleto;
    private String enderecoDestino; // Caso não tenha o endereço completo
    private List<ParametroOrcamento> parametros;
    private List<Long> idsCondicoes;

}
