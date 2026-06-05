package com.baseapplication.core.dao;

import com.baseapplication.core.model.PreferenciaNotificacaoMembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PreferenciaNotificacaoMembroDao extends JpaRepository<PreferenciaNotificacaoMembro, Long> {
    
    @Query("SELECT p FROM PreferenciaNotificacaoMembro p WHERE p.idBanda = :idBanda AND p.idUsuario = :idUsuario")
    Optional<PreferenciaNotificacaoMembro> buscarPorBandaEUsuario(Long idBanda, Long idUsuario);
    
    @Query("SELECT p FROM PreferenciaNotificacaoMembro p WHERE p.idBanda = :idBanda AND p.idMembroFantasma = :idMembroFantasma")
    Optional<PreferenciaNotificacaoMembro> buscarPorBandaEMembroFantasma(Long idBanda, Long idMembroFantasma);
    
    @Query("SELECT p FROM PreferenciaNotificacaoMembro p WHERE p.idBanda = :idBanda")
    List<PreferenciaNotificacaoMembro> buscarPorBanda(Long idBanda);
}
