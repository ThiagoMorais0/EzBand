package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MUSICO_BANDA")
public class MusicoBanda {

    @EmbeddedId
    private MusicoBandaId id;
    private String instrumentos;
    @Enumerated(EnumType.STRING)
    private PermissaoMusico permissao;
}
