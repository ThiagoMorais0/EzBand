package com.baseapplication.core.dao;

import com.baseapplication.core.model.CompromissoPessoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompromissoPessoalDao extends JpaRepository<CompromissoPessoal, Long> {

    List<CompromissoPessoal> findByUsuarioIdAndDataBetween(Long idUsuario, LocalDate inicio, LocalDate fim);

    List<CompromissoPessoal> findByUsuarioIdAndData(Long idUsuario, LocalDate data);

    boolean existsByUsuarioIdAndData(Long idUsuario, LocalDate data);
}
