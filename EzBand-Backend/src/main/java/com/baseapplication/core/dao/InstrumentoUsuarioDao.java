package com.baseapplication.core.dao;

import com.baseapplication.core.model.InstrumentoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InstrumentoUsuarioDao extends JpaRepository<InstrumentoUsuario, Long> {

    List<InstrumentoUsuario> findByIdUsuarioOrderByFavoritoDescNomeAsc(Long idUsuario);

    @Modifying
    @Query("UPDATE InstrumentoUsuario i SET i.favorito = false WHERE i.idUsuario = :idUsuario")
    void clearFavoritosDoUsuario(Long idUsuario);
}
