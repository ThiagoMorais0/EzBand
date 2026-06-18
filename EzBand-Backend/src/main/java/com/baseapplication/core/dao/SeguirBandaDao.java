package com.baseapplication.core.dao;

import com.baseapplication.core.model.SeguirBanda;
import com.baseapplication.core.model.SeguirBandaId;
import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeguirBandaDao extends JpaRepository<SeguirBanda, SeguirBandaId> {
    boolean existsByIdIdUsuarioAndIdIdBanda(Long idUsuario, Long idBanda);
    long countByIdIdBanda(Long idBanda);

    @Query("SELECT u FROM Usuario u WHERE u.id IN (SELECT sb.id.idUsuario FROM SeguirBanda sb WHERE sb.id.idBanda = :idBanda)")
    List<Usuario> buscarSeguidoresDaBanda(@Param("idBanda") Long idBanda);
}
