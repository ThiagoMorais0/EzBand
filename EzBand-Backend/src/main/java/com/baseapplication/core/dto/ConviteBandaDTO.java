package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConviteBandaDTO {
    private String contato;
    private TipoContato tipoContato;
    private Long idBanda;
    private Long idUsuarioRemetente;
}
