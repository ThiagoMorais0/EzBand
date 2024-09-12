package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "NOTIFICACAO_ENSAIO")
public class NotificacaoEnsaio extends Notificacao {
    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;
    @OneToOne
    @JoinColumn(name = "id_ensaio")
    private Ensaio ensaio;
    private Date dataEnsaio;


}
