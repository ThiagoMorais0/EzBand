package com.baseapplication.core.dto;

import com.baseapplication.core.model.ConfiguracaoNotificacaoUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoNotificacaoUsuarioDTO {

    private Long id;
    private Long idUsuario;
    private Boolean receberNotificacoesWhatsapp = false;
    private List<Integer> diasAntecedenciaLembrete = new ArrayList<>();
    private Integer diaSemanaResumoSemanal;

    public ConfiguracaoNotificacaoUsuarioDTO(ConfiguracaoNotificacaoUsuario entity) {
        this.id = entity.getId();
        this.idUsuario = entity.getIdUsuario();
        this.receberNotificacoesWhatsapp = entity.getReceberNotificacoesWhatsapp();
        this.diasAntecedenciaLembrete = new ArrayList<>(entity.getDiasAntecedenciaLembrete());
        this.diaSemanaResumoSemanal = entity.getDiaSemanaResumoSemanal();
    }

    public ConfiguracaoNotificacaoUsuario toEntity() {
        ConfiguracaoNotificacaoUsuario entity = new ConfiguracaoNotificacaoUsuario();
        entity.setId(this.id);
        entity.setIdUsuario(this.idUsuario);
        entity.setReceberNotificacoesWhatsapp(this.receberNotificacoesWhatsapp);
        entity.setDiasAntecedenciaLembrete(new ArrayList<>(this.diasAntecedenciaLembrete));
        entity.setDiaSemanaResumoSemanal(this.diaSemanaResumoSemanal);
        return entity;
    }
}
