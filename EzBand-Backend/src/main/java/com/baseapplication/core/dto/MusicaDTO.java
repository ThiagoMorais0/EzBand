package com.baseapplication.core.dto;

import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class MusicaDTO {
    private String titulo;
    private String artista;
    private String descricao;
    private String observacao;
    private Time duracao;
    private Integer tonalidade;
    private String urlYoutube;
    private String urlSpotify;

    public MusicaDTO(Musica musica){
        BeanUtils.copyProperties(musica, this);
    }

    public Musica toEntity(){
        Musica musica = new Musica();
        musica.setTonalidade(Tonalidade.encontrarPeloNumero(tonalidade));
        BeanUtils.copyProperties(this, musica);
        return musica;
    }
}
