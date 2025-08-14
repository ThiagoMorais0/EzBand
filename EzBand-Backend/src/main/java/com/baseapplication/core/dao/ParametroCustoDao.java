package com.baseapplication.core.dao;

import com.baseapplication.core.model.ParametroCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParametroCustoDao extends JpaRepository<ParametroCusto, Long> {
    @Query(value = "SELECT p FROM ParametroCusto p WHERE p.banda.id = :idBanda")
    List<ParametroCusto> buscarPorIdBanda(Long idBanda);

    ParametroCusto findByNome(String nome);
}
