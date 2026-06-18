package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "SEGUIR_BANDA")
public class SeguirBanda {
    @EmbeddedId
    private SeguirBandaId id;
    private LocalDate dataSeguindo;
}
