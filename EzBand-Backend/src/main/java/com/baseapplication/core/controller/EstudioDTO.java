package com.baseapplication.core.controller;

import com.baseapplication.core.dto.EnderecoDTO;
import com.baseapplication.core.dto.EquipamentoEstudioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.ServicoEstudioDTO;
import com.baseapplication.core.model.EquipamentoEstudio;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.ServicoEstudio;
import com.baseapplication.core.model.embedded.Endereco;
import com.baseapplication.core.utils.Context;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class EstudioDTO {
    private EnderecoDTO endereco = new EnderecoDTO();
    private Long id;
    private String nome;
    private String descricao;
    private String horarioInicioFuncionamento;
    private String horarioFinalFuncionamento;
    private List<ServicoEstudioDTO> servicos = new ArrayList<>();
    private List<EquipamentoEstudioDTO> equipamentos = new ArrayList<>();
    private InfoUsuarioDTO proprietario;

    public EstudioDTO(Estudio entity){
        BeanUtils.copyProperties(entity, this);
        this.setEndereco(new EnderecoDTO(entity.getEndereco()));
        this.setProprietario(InfoUsuarioDTO.toDTO(entity.getProprietario()));
        this.setServicos(entity.getServicos().stream().map(ServicoEstudioDTO::new).toList());
        this.setEquipamentos(entity.getEquipamentos().stream().map(EquipamentoEstudioDTO::new).toList());
    }

    public void toEntity(Estudio entity) {
        BeanUtils.copyProperties(this, entity);
        if (entity.getEndereco() == null) {
            entity.setEndereco(new Endereco());
        }
        BeanUtils.copyProperties(this.endereco, entity.getEndereco());


        List<ServicoEstudio> servicosEntity = servicos.stream()
                .map(dto -> {
                    ServicoEstudio s = dto.toEntity();
                    s.setEstudio(entity);
                    return s;
                }).toList();

        List<EquipamentoEstudio> equipamentosEntity = equipamentos.stream()
                .map(dto -> {
                    EquipamentoEstudio e = dto.toEntity();
                    e.setEstudio(entity);
                    return e;
                }).toList();

        entity.setServicos(servicosEntity);
        entity.setEquipamentos(equipamentosEntity);
    }

}
