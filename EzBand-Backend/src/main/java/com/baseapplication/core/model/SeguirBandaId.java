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
public class SeguirBandaId implements Serializable {
    private Long idUsuario;
    private Long idBanda;
}
