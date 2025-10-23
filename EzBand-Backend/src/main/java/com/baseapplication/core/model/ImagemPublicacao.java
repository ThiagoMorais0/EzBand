package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Publicacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
@Setter
@Table(name = "imagem_publicacao")
@AllArgsConstructor
@NoArgsConstructor
public class ImagemPublicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String url;

    @ManyToOne
    @JoinColumn(name = "id_publicacao")
    private Publicacao publicacao;
}
