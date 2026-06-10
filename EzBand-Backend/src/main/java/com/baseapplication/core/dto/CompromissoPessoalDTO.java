package com.baseapplication.core.dto;

import com.baseapplication.core.model.CompromissoPessoal;
import com.baseapplication.core.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompromissoPessoalDTO {
    private Long id;
    private String data;
    private String descricao;

    public CompromissoPessoalDTO(CompromissoPessoal c) {
        this.id = c.getId();
        this.data = DateUtils.localDateToString(c.getData());
        this.descricao = c.getDescricao();
    }
}
