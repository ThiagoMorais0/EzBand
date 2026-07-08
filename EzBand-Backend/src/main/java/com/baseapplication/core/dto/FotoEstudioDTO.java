package com.baseapplication.core.dto;

import com.baseapplication.core.model.FotoEstudio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FotoEstudioDTO {
    private Long id;
    private String url;

    public FotoEstudioDTO(FotoEstudio entity) {
        this.id = entity.getId();
        this.url = entity.getUrl();
    }
}