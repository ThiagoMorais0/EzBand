package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoItemRepertorio;
import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REPERTORIO_EVENTO")
public class RepertorioEvento {

    @EmbeddedId
    private RepertorioEventoId id;

    @Embedded
    private Musica musica;


    private String bloco;

    /**
     * O que este item é. Um MOMENTO guarda o texto em {@code musica.titulo} e a estimativa em
     * {@code musica.duracao}; o resto do embutido fica nulo.
     *
     * <p>O NOT NULL mora só na migration, junto com o DEFAULT que o torna aplicável a uma
     * tabela que já tem linhas. Declará-lo aqui também quebraria o ddl-auto=update do ambiente
     * local: o Hibernate tentaria um `add column ... not null` sem default, o comando falharia
     * na tabela já populada e ele seguiria em silêncio — a coluna simplesmente não apareceria.
     * O getter tolera nulo pelo mesmo motivo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item")
    private TipoItemRepertorio tipoItem = TipoItemRepertorio.MUSICA;

    public TipoItemRepertorio getTipoItem() {
        return tipoItem == null ? TipoItemRepertorio.MUSICA : tipoItem;
    }

    public boolean isMomento() {
        return getTipoItem() == TipoItemRepertorio.MOMENTO;
    }
}
