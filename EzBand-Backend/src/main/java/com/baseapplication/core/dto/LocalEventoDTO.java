package com.baseapplication.core.dto;

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
    private Long id;
    private String nome;
    private String bio;
    private String urlFotoPerfil;
    private String horarioCostumeiroPowerSound;
    private String horarioCostumeiroInicioShow;
    private EnderecoDTO endereco = new EnderecoDTO();
    private List<EquipamentoLocalEventoDTO> equipamentos = new ArrayList<>();
    private List<SocioLocalEventoDTO> socios = new ArrayList<>();
    private InfoUsuarioDTO proprietario;

    public LocalEventoDTO(LocalEvento entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.bio = entity.getBio();
        this.urlFotoPerfil = entity.getUrlFotoPerfil();
        this.horarioCostumeiroPowerSound = entity.getHorarioCostumeiroPowerSound();
        this.horarioCostumeiroInicioShow = entity.getHorarioCostumeiroInicioShow();
        this.endereco = new EnderecoDTO(entity.getEndereco());
        this.proprietario = InfoUsuarioDTO.toDTO(entity.getProprietario());
        if (entity.getEquipamentos() != null)
            this.equipamentos = entity.getEquipamentos().stream().map(EquipamentoLocalEventoDTO::new).toList();
        if (entity.getSocios() != null)
            this.socios = entity.getSocios().stream().map(SocioLocalEventoDTO::new).toList();
    }

    public void toEntity(LocalEvento entity) {
        entity.setNome(this.nome);
        entity.setBio(this.bio);
        entity.setHorarioCostumeiroPowerSound(this.horarioCostumeiroPowerSound);
        entity.setHorarioCostumeiroInicioShow(this.horarioCostumeiroInicioShow);
        if (entity.getEndereco() == null) entity.setEndereco(new Endereco());
        if (this.endereco != null) BeanUtils.copyProperties(this.endereco, entity.getEndereco());
    }
}
