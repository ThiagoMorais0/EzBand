package com.baseapplication.core.dto;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import jakarta.persistence.Embedded;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CadastroBandaDTO {
    private String nome;
    private String descricao;
    private String categoria;
    private String instrumento;
}
