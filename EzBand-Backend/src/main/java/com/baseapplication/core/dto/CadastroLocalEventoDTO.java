package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoLocalEvento;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.utils.Context;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CadastroLocalEventoDTO {
    private EnderecoDTO endereco;
    private String nome;
    private String bio;
    private String horarioCostumeiroPowerSound;
    private String horarioCostumeiroInicioShow;
    private List<EquipamentoLocalEventoDTO> equipamentos = new ArrayList<>();

    public LocalEvento toEntity() {
        LocalEvento entity = new LocalEvento();
        entity.setNome(this.nome);
        entity.setBio(this.bio);
        entity.setHorarioCostumeiroPowerSound(this.horarioCostumeiroPowerSound);
        entity.setHorarioCostumeiroInicioShow(this.horarioCostumeiroInicioShow);
        entity.setProprietario(Context.getUsuarioLogado());

        if (this.endereco != null) {
            BeanUtils.copyProperties(this.endereco, entity.getEndereco());
        }

        List<EquipamentoLocalEvento> equipamentosEntity = equipamentos.stream()
                .map(dto -> {
                    EquipamentoLocalEvento e = dto.toEntity();
                    e.setLocalEvento(entity);
                    return e;
                }).collect(Collectors.toList());
        entity.setEquipamentos(equipamentosEntity);

        return entity;
    }
}
