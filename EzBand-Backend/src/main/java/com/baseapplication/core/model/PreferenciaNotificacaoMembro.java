package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PREFERENCIA_NOTIFICACAO_MEMBRO")
public class PreferenciaNotificacaoMembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_BANDA", nullable = false)
    private Long idBanda;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_MEMBRO_FANTASMA")
    private Long idMembroFantasma;

    @Column(name = "NOTIFICAR_NOVO_EVENTO")
    private Boolean notificarNovoEvento = true;

    @ElementCollection
    @CollectionTable(
        name = "DIAS_ANTECEDENCIA_NOTIFICACAO",
        joinColumns = @JoinColumn(name = "ID_PREFERENCIA")
    )
    @Column(name = "DIAS")
    private List<Integer> diasAntecedenciaNotificacao = new ArrayList<>();
}
