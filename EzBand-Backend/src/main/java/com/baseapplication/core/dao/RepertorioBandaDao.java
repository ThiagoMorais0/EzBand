package com.baseapplication.core.dao;

import com.baseapplication.core.model.RepertorioBanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepertorioBandaDao extends JpaRepository<RepertorioBanda, Long> {

    List<RepertorioBanda> findByBandaId(Long idBanda);

    @Query(value = "select max(indice) from repertorio_banda where id_banda = :idBanda", nativeQuery = true)
    Integer buscarUltimoIndice(Long idBanda);
}
