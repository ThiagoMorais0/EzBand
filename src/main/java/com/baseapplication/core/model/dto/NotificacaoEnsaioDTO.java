package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.NotificacaoEnsaio;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoEnsaioDTO extends NotificacaoDTO {

    private BandaDTO banda;
    private EnsaioDTO ensaio;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private Date dataEnsaio;

    public NotificacaoEnsaioDTO(NotificacaoEnsaio notificacaoEnsaio){
        BeanUtils.copyProperties(notificacaoEnsaio, this);
    }

}
