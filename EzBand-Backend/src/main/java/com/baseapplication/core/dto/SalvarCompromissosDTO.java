package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SalvarCompromissosDTO {
    private List<String> datas;
    private String descricao;
}
