package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoEstudio;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.ServicoEstudio;
import com.baseapplication.core.utils.Context;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CadastroEstudioDTO {
    private EnderecoDTO endereco;
    private String nome;
    private String descricao;
    private String horarioInicioFuncionamento;
    private String horarioFinalFuncionamento;
    private List<ServicoEstudioDTO> servicos = new ArrayList<>();
    private List<EquipamentoEstudioDTO> equipamentos = new ArrayList<>();
    private List<String> diasFuncionamento = new ArrayList<>();
    private boolean exigirConfirmacaoEnsaios = false;

    public Estudio toEntity() {
        Estudio entity = new Estudio();
        BeanUtils.copyProperties(this, entity);

        if(this.endereco != null)
            BeanUtils.copyProperties(this.endereco, entity.getEndereco());

        // Proprietário logado
        entity.setProprietario(Context.getUsuarioLogado());

        // Serviços
        List<ServicoEstudio> servicosEntity = new ArrayList<>(servicos.stream()
                .map(dto -> {
                    ServicoEstudio s = dto.toEntity();
                    s.setEstudio(entity); // ESSENCIAL
                    return s;
                }).toList());

        // Equipamentos
        List<EquipamentoEstudio> equipamentosEntity = new ArrayList<>(equipamentos.stream()
                .map(dto -> {
                    EquipamentoEstudio e = dto.toEntity();
                    e.setEstudio(entity); // ESSENCIAL
                    return e;
                }).toList());

        entity.setServicos(servicosEntity);
        entity.setEquipamentos(equipamentosEntity);
        entity.setDiasFuncionamento(new ArrayList<>(this.diasFuncionamento));
        entity.setExigirConfirmacaoEnsaios(this.exigirConfirmacaoEnsaios);

        if (this.horarioInicioFuncionamento != null && !this.horarioInicioFuncionamento.isBlank())
            entity.setHorarioInicioFuncionamento(LocalDateTime.parse(this.horarioInicioFuncionamento));
        if (this.horarioFinalFuncionamento != null && !this.horarioFinalFuncionamento.isBlank())
            entity.setHorarioFinalFuncionamento(LocalDateTime.parse(this.horarioFinalFuncionamento));

        return entity;
    }


}
