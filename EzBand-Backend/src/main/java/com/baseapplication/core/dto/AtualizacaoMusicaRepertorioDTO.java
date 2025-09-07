package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizacaoMusicaRepertorioDTO {
    private Integer indice;
    private Long idEvento;
    private TipoEvento tipoEvento;
    private MusicaDTO musica = new MusicaDTO();


}
