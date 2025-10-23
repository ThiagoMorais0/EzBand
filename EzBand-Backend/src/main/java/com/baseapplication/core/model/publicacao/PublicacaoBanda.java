package com.baseapplication.core.model.publicacao;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.superClasses.Publicacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("PUBLI_BANDA")
@Getter
@Setter
public class PublicacaoBanda extends Publicacao {

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;
}
