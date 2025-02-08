package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailDTO {
    private String assunto;
    private String mensagem;
    private String destinatario;
}
