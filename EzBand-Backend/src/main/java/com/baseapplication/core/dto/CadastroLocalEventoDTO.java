package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoLocalEvento;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.utils.Context;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CadastroLocalEventoDTO {
    private EnderecoDTO endereco;
    private String nome;
    private String descricao;
    private String horarioInicioFuncionamento;
    private String horarioFinalFuncionamento;
    private List<EquipamentoLocalEventoDTO> equipamentos = new ArrayList<>();

    public LocalEvento toEntity() {
        LocalEvento entity = new LocalEvento();
        BeanUtils.copyProperties(this, entity);

        if(this.endereco != null)
            BeanUtils.copyProperties(this.endereco, entity.getEndereco());

        // Proprietário logado
        entity.setProprietario(Context.getUsuarioLogado());

        // Equipamentos
        List<EquipamentoLocalEvento> equipamentosEntity = equipamentos.stream()
                .map(dto -> {
                    EquipamentoLocalEvento e = dto.toEntity();
                    e.setLocalEvento(entity); // ESSENCIAL
                    return e;
                }).toList();

        entity.setEquipamentos(equipamentosEntity);

        return entity;
    }


}
