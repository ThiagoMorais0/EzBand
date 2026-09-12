package com.baseapplication.core.dao;

import com.baseapplication.core.model.AudioMusica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AudioMusicaDao extends JpaRepository<AudioMusica, Long> {

    List<AudioMusica> findByIdBanda(Long idBanda);

    Optional<AudioMusica> findByIdBandaAndChaveMusica(Long idBanda, String chaveMusica);

    /** Soma da quota. COALESCE porque SUM sobre zero linhas volta nulo. */
    @Query("SELECT COALESCE(SUM(a.tamanhoBytes), 0) FROM AudioMusica a WHERE a.idBanda = :idBanda")
    long somarBytesDaBanda(Long idBanda);

    /**
     * Quantas linhas apontam para o mesmo objeto no storage. Deletar o objeto so e seguro
     * quando esta contagem chega a zero -- o dedup faz duas musicas da banda compartilharem um
     * arquivo, e apagar uma nao pode calar a outra.
     *
     * <p>Conta por URL, nao por hash: duas bandas que enviem o mesmo arquivo tem o mesmo
     * sha256 e objetos separados, e contar por hash faria a exclusao de uma achar que a outra
     * ainda usa o objeto -- deixando lixo no storage para sempre.
     */
    long countByUrl(String url);
}
