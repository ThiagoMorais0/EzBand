package com.baseapplication.core.dto;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InfoUsuarioPainelDTO {
    List<BandaDTO> bandas;
    Integer quantidadeNotificacoes;
    Integer quantidadeParticipacoesEspeciais;
    Integer quantidadeProximosEventos;
}
