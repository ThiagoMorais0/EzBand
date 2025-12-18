package com.baseapplication.core.dto;

import com.baseapplication.core.model.ItemChecklistBanda;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemChecklistBandaDTO {
    
    private Long id;
    private Long idBanda;
    private String nome;
    private String descricao;
    private String dataCriacao;
    private Boolean ativo;

    public ItemChecklistBandaDTO(ItemChecklistBanda item) {
        this.id = item.getId();
        this.idBanda = item.getIdBanda();
        this.nome = item.getNome();
        this.descricao = item.getDescricao();
        this.dataCriacao = DateUtils.localDateTimeToString(item.getDataCriacao());
        this.ativo = item.getAtivo();
    }
}
