package com.baseapplication.core.dao;

import com.baseapplication.core.model.ImagemPublicacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ImagemPublicacaoDao extends JpaRepository<ImagemPublicacao, Long> {

    @Transactional
    @Modifying
    @Query(value = "delete from imagem_publicacao where id_publicacao = :idPublicacao", nativeQuery = true)
    int deletarPorIdPublicacao(Long idPublicacao);
}
