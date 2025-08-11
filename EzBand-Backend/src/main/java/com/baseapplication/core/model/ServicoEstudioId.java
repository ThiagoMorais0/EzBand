package com.baseapplication.core.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ServicoEstudioId implements Serializable {
    private Long idEstudio;
    private Long idServico;
}
