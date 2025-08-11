package com.baseapplication.core.dao;

import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudioDao extends JpaRepository<Estudio, Long> {

    @Query(value = "SELECT e FROM Estudio e WHERE e.proprietario.id = :idProprietario")
    List<Estudio> findByProprietario(Long idProprietario);
}
