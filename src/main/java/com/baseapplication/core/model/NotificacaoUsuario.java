package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "NOTIFICACAO_USUARIO")
public class NotificacaoUsuario extends Notificacao {

}
