package com.baseapplication.core.dao;

import com.baseapplication.core.model.ConviteExternoBanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConviteExternoBandaDao extends JpaRepository<ConviteExternoBanda, Long> {

    Optional<ConviteExternoBanda> findByToken(String token);

    Optional<ConviteExternoBanda> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
