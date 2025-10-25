package com.baseapplication.core.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class RelacionamentoSeguidorId implements Serializable {
    private Long idSeguidor;
    private Long idSeguido;
}
