package com.baseapplication.core.controller;

import com.baseapplication.core.dto.EnderecoDTO;
import com.baseapplication.core.dto.EquipamentoEstudioDTO;
import com.baseapplication.core.dto.ServicoEstudioDTO;
import com.baseapplication.core.model.EquipamentoEstudio;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.ServicoEstudio;
import com.baseapplication.core.utils.Context;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
public class CadastroEstudioDTO {
    private EnderecoDTO endereco;
    private String nome;
    private String descricao;
    private String horarioInicioFuncionamento;
    private String horarioFinalFuncionamento;
    private List<ServicoEstudioDTO> servicos;
    private List<EquipamentoEstudioDTO> equipamentos;

    public Estudio toEntity() {
        Estudio entity = new Estudio();
        BeanUtils.copyProperties(this, entity);
        BeanUtils.copyProperties(this.endereco, entity.getEndereco());

        // Proprietário logado
        entity.setProprietario(Context.getUsuarioLogado());

        // Serviços
        List<ServicoEstudio> servicosEntity = servicos.stream()
                .map(dto -> {
                    ServicoEstudio s = dto.toEntity();
                    s.setEstudio(entity); // ESSENCIAL
                    return s;
                }).toList();

        // Equipamentos
        List<EquipamentoEstudio> equipamentosEntity = equipamentos.stream()
                .map(dto -> {
                    EquipamentoEstudio e = dto.toEntity();
                    e.setEstudio(entity); // ESSENCIAL
                    return e;
                }).toList();

        entity.setServicos(servicosEntity);
        entity.setEquipamentos(equipamentosEntity);

        return entity;
    }


}
