package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoLocalEvento;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.embedded.Endereco;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LocalEventoDTO {
    private EnderecoDTO endereco = new EnderecoDTO();
    private Long id;
    private String nome;
    private String descricao;
    private String horarioInicioFuncionamento;
    private String horarioFinalFuncionamento;
    private List<EquipamentoLocalEventoDTO> equipamentos = new ArrayList<>();
    private InfoUsuarioDTO proprietario;

    public LocalEventoDTO(LocalEvento entity){
        BeanUtils.copyProperties(entity, this);
        this.setEndereco(new EnderecoDTO(entity.getEndereco()));
        this.setProprietario(InfoUsuarioDTO.toDTO(entity.getProprietario()));
        this.setEquipamentos(entity.getEquipamentos().stream().map(EquipamentoLocalEventoDTO::new).toList());
    }

    public void toEntity(LocalEvento entity) {
        BeanUtils.copyProperties(this, entity);
        if (entity.getEndereco() == null) {
            entity.setEndereco(new Endereco());
        }
        BeanUtils.copyProperties(this.endereco, entity.getEndereco());


        List<EquipamentoLocalEvento> equipamentosEntity = equipamentos.stream()
                .map(dto -> {
                    EquipamentoLocalEvento e = dto.toEntity();
                    e.setLocalEvento(entity);
                    return e;
                }).toList();

        entity.setEquipamentos(equipamentosEntity);
    }

}
