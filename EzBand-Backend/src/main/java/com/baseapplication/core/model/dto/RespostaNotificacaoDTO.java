package com.baseapplication.core.model.dto;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespostaNotificacaoDTO {
    private String acao;
    private String mensagem;

    public RespostaNotificacao toEntity(Long idNotificacao){
        RespostaNotificacao entity = new RespostaNotificacao();
        entity.setNotificacaoId(idNotificacao);
        entity.setAcao(AcaoResposta.findByName(acao));
        entity.setMensagem(mensagem);
        return entity;
    }
}

