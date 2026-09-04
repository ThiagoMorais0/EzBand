package com.baseapplication.core.dao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.SessaoAoVivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoAoVivoDao extends JpaRepository<SessaoAoVivo, Long> {

    @Query("select s from SessaoAoVivo s left join fetch s.faixas "
            + "where s.idEvento = :idEvento and s.tipoEvento = :tipoEvento "
            + "order by s.iniciadaEm desc")
    List<SessaoAoVivo> buscarPorEvento(Long idEvento, TipoEvento tipoEvento);

    Optional<SessaoAoVivo> findFirstByIdEventoAndTipoEventoOrderByIniciadaEmDesc(Long idEvento, TipoEvento tipoEvento);

    boolean existsByIdEventoAndTipoEventoAndIniciadaEm(Long idEvento, TipoEvento tipoEvento, java.time.LocalDateTime iniciadaEm);
}
