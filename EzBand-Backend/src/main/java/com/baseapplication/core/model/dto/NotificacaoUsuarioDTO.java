package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.model.superClasses.Notificacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class NotificacaoUsuarioDTO extends NotificacaoDTO {

    public NotificacaoUsuarioDTO(Notificacao notificacao){
        BeanUtils.copyProperties(notificacao, this);
    }

}
