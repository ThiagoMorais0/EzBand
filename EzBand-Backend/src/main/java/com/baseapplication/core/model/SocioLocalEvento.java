package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SOCIO_LOCAL_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocioLocalEvento {

    @EmbeddedId
    private SocioLocalEventoId id;

    @ManyToOne
    @MapsId("idLocalEvento")
    @JoinColumn(name = "ID_LOCAL_EVENTO")
    private LocalEvento localEvento;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO")
    private Usuario usuario;

    private Boolean aprovaShows = false;
}
