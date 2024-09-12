package com.baseapplication.core.model.dto.superClasses;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.NotificacaoEnsaio;
import com.baseapplication.core.model.NotificacaoShow;
import com.baseapplication.core.model.NotificacaoUsuario;
import com.baseapplication.core.model.dto.NotificacaoEnsaioDTO;
import com.baseapplication.core.model.dto.NotificacaoShowDTO;
import com.baseapplication.core.model.dto.NotificacaoUsuarioDTO;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.BeanUtils;

import java.util.Date;

public class NotificacaoDTO {
    private Long id;

    private Long remetente;
    private String mensagem;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private Date data;

    public NotificacaoDTO toDTO(Notificacao notificacao) {
        if (notificacao instanceof NotificacaoShow) {
            return new NotificacaoShowDTO((NotificacaoShow) notificacao);
        } else if (notificacao instanceof NotificacaoEnsaio) {
            return new NotificacaoEnsaioDTO((NotificacaoEnsaio) notificacao);
        } else if (notificacao instanceof NotificacaoUsuario) {
            return new NotificacaoUsuarioDTO(notificacao);
        } else {
            throw new IllegalArgumentException("Tipo de notificação desconhecido: " + notificacao.getClass().getName());
        }
    }

}
