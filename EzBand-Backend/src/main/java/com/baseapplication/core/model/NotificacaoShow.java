package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "NOTIFICACAO_SHOW")
public class NotificacaoShow extends Notificacao {
    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;
    @OneToOne
    @JoinColumn(name = "id_show")
    private Show show;
    private LocalDate dataEvento;


}
