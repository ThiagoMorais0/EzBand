package com.baseapplication.core.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RelacionamentoSeguidorId {
    private Long idSeguidor;
    private Long idSeguido;
}
