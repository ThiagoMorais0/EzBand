package com.baseapplication.core.dto;

import com.baseapplication.core.enums.CategoriaDenuncia;
import com.baseapplication.core.enums.TipoAlvoDenuncia;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DenunciaDTO {

    private TipoAlvoDenuncia tipoAlvo;
    private Long idAlvo;
    private CategoriaDenuncia categoria;
    private String descricao;
}
