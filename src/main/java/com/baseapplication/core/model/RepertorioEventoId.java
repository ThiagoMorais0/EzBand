package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RepertorioEventoId {
    @Column(name = "id_evento", nullable = false)
    private Long idEvento;

    @Column(name = "id_banda", nullable = false)
    private Long idBanda;

    private TipoEvento tipoEvento;

}
