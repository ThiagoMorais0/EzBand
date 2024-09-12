package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ENSAIO")
public class Ensaio extends Evento {

    private BigDecimal valor;
}
