package com.baseapplication.core.dto;

import com.baseapplication.core.model.ItemChecklistUsuario;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemChecklistUsuarioDTO {
    
    private Long id;
    private Long idUsuario;
    private String nome;
    private String descricao;
    private String dataCriacao;
    private Boolean ativo;

    public ItemChecklistUsuarioDTO(ItemChecklistUsuario item) {
        this.id = item.getId();
        this.idUsuario = item.getIdUsuario();
        this.nome = item.getNome();
        this.descricao = item.getDescricao();
        this.dataCriacao = DateUtils.localDateTimeToString(item.getDataCriacao());
        this.ativo = item.getAtivo();
    }
}
