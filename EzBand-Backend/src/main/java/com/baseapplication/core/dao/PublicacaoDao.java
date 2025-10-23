package com.baseapplication.core.dao;

import com.baseapplication.core.model.superClasses.Publicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PublicacaoDao extends JpaRepository<Publicacao, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from publicacao where id = :idPublicacao", nativeQuery = true)
    int deletarPeloId(Long idPublicacao);
}
