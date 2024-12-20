package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RepertorioEventoId {
    @Column(name = "id_evento", nullable = false)
    private Long idEvento;

    @Column(name = "id_banda", nullable = false)
    private Long idBanda;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

}
