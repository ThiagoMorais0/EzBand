package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MUSICO_BANDA")
public class MusicoBanda {

    @EmbeddedId
    private MusicoBandaId id;
    private String instrumentos;

    @ElementCollection(targetClass = PermissaoMusico.class)
    @CollectionTable(
            name = "permissao_musico_banda",
            joinColumns = {
                    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO"),
                    @JoinColumn(name = "ID_BANDA", referencedColumnName = "ID_BANDA")
            }
    )
    @Enumerated(EnumType.STRING)
    private List<PermissaoMusico> permissoes = new ArrayList<>();

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne
    @MapsId("idBanda")
    @JoinColumn(name = "ID_BANDA", insertable = false, updatable = false)
    private Banda banda;
}
