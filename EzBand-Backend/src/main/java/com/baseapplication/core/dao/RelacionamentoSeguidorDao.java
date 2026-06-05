package com.baseapplication.core.dao;

import com.baseapplication.core.enums.StatusSeguidor;
import com.baseapplication.core.model.RelacionamentoSeguidor;
import com.baseapplication.core.model.RelacionamentoSeguidorId;
import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelacionamentoSeguidorDao extends JpaRepository<RelacionamentoSeguidor, RelacionamentoSeguidorId> {

    @Query("SELECT r FROM RelacionamentoSeguidor r WHERE r.id.idSeguidor = :idSeguidor AND r.id.idSeguido = :idSeguido")
    Optional<RelacionamentoSeguidor> buscarRelacionamento(@Param("idSeguidor") Long idSeguidor, @Param("idSeguido") Long idSeguido);

    @Query("SELECT COUNT(r) > 0 FROM RelacionamentoSeguidor r WHERE r.id.idSeguidor = :idSeguidor AND r.id.idSeguido = :idSeguido AND r.status = :status")
    boolean existeRelacionamento(@Param("idSeguidor") Long idSeguidor, @Param("idSeguido") Long idSeguido, @Param("status") StatusSeguidor status);

    //Buscar usuários que seguem o usuário, mas somente os que ele segue de volta
    @Query("""
        SELECT u
        FROM RelacionamentoSeguidor r1
        JOIN RelacionamentoSeguidor r2
            ON r1.id.idSeguidor = r2.id.idSeguido
        JOIN Usuario u
            ON r1.id.idSeguidor = u.id
        WHERE r1.id.idSeguido = :idUsuario
          AND r2.id.idSeguidor = :idUsuario
    """)
    List<Usuario> buscarAmigos(Long idUsuario);

    @Query("SELECT u FROM RelacionamentoSeguidor r JOIN Usuario u ON r.id.idSeguidor = u.id WHERE r.id.idSeguido = :idUsuario AND r.status = :status")
    List<Usuario> buscarSeguidores(@Param("idUsuario") Long idUsuario, @Param("status") StatusSeguidor status);

    @Query("SELECT COUNT(r) FROM RelacionamentoSeguidor r WHERE r.id.idSeguido = :idUsuario AND r.status = :status")
    Long contarSeguidores(@Param("idUsuario") Long idUsuario, @Param("status") StatusSeguidor status);

    @Query("SELECT COUNT(r) FROM RelacionamentoSeguidor r WHERE r.id.idSeguidor = :idUsuario AND r.status = :status")
    Long contarSeguindo(@Param("idUsuario") Long idUsuario, @Param("status") StatusSeguidor status);
}
