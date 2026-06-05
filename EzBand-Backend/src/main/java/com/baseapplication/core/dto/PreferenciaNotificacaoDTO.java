package com.baseapplication.core.dto;

import com.baseapplication.core.model.PreferenciaNotificacaoMembro;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaNotificacaoDTO {
    private Long id;
    private Long idBanda;
    private Long idUsuario;
    private Long idMembroFantasma;
    private Boolean notificarNovoEvento;
    private List<Integer> diasAntecedenciaNotificacao = new ArrayList<>();

    public PreferenciaNotificacaoDTO(PreferenciaNotificacaoMembro entity) {
        BeanUtils.copyProperties(entity, this);
    }

    public PreferenciaNotificacaoMembro toEntity() {
        PreferenciaNotificacaoMembro entity = new PreferenciaNotificacaoMembro();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
