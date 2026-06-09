package com.baseapplication.core.dao;

import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.baseapplication.core.dto.BuscaEstudioDTO;

@Repository
public interface EstudioDao extends JpaRepository<Estudio, Long> {

    @Query(value = "SELECT e FROM Estudio e WHERE e.proprietario.id = :idProprietario")
    List<Estudio> findByProprietario(Long idProprietario);

    @Query("SELECT e FROM Estudio e JOIN e.socios s WHERE s.id = :idSocio")
    List<Estudio> findBySocio(@Param("idSocio") Long idSocio);

    @Query("SELECT e FROM Estudio e \n" +
            "WHERE (:#{#dto.nome} IS NULL OR :#{#dto.nome} = '' OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :#{#dto.nome}, '%'))) AND \n" +
            "(:#{#dto.cidade} IS NULL OR :#{#dto.cidade} = '' OR LOWER(e.endereco.cidade) LIKE LOWER(CONCAT('%', :#{#dto.cidade}, '%'))) AND \n" +
            "(:#{#dto.bairro} IS NULL OR :#{#dto.bairro} = '' OR LOWER(e.endereco.bairro) LIKE LOWER(CONCAT('%', :#{#dto.bairro}, '%'))) AND \n" +
            "(:#{#dto.rua} IS NULL OR :#{#dto.rua} = '' OR LOWER(e.endereco.rua) LIKE LOWER(CONCAT('%', :#{#dto.rua}, '%'))) AND \n" +
            "(:#{#dto.UF} IS NULL OR :#{#dto.UF} = '' OR LOWER(e.endereco.estado) LIKE LOWER(CONCAT('%', :#{#dto.UF}, '%')))")
    List<Estudio> buscarSugestoes(@Param("dto") BuscaEstudioDTO dto);
}
