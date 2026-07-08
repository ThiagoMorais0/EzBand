package com.baseapplication.core.dao;

import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.model.LocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalEventoDao extends JpaRepository<LocalEvento, Long> {

    @Query("SELECT e FROM LocalEvento e WHERE e.proprietario.id = :idProprietario")
    List<LocalEvento> findByProprietario(Long idProprietario);

    @Query("SELECT e FROM LocalEvento e JOIN e.socios s WHERE s.usuario.id = :idUsuario")
    List<LocalEvento> findBySocio(Long idUsuario);

    @Query("SELECT e FROM LocalEvento e " +
            "WHERE (:#{#dto.nome} IS NULL OR :#{#dto.nome} = '' OR LOWER(e.nome) LIKE LOWER(CONCAT('%', :#{#dto.nome}, '%'))) AND " +
            "(:#{#dto.cidade} IS NULL OR :#{#dto.cidade} = '' OR LOWER(e.endereco.cidade) LIKE LOWER(CONCAT('%', :#{#dto.cidade}, '%'))) AND " +
            "(:#{#dto.bairro} IS NULL OR :#{#dto.bairro} = '' OR LOWER(e.endereco.bairro) LIKE LOWER(CONCAT('%', :#{#dto.bairro}, '%'))) AND " +
            "(:#{#dto.rua} IS NULL OR :#{#dto.rua} = '' OR LOWER(e.endereco.rua) LIKE LOWER(CONCAT('%', :#{#dto.rua}, '%'))) AND " +
            "(:#{#dto.UF} IS NULL OR :#{#dto.UF} = '' OR LOWER(e.endereco.estado) LIKE LOWER(CONCAT('%', :#{#dto.UF}, '%')))")
    List<LocalEvento> buscarSugestoes(@Param("dto") BuscaLocalEventoDTO dto);
}
