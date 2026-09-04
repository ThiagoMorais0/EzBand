package com.baseapplication.core.service.impl;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface RepertorioEventoDao extends JpaRepository<RepertorioEvento, RepertorioEventoId> {

    @Transactional
    @Modifying
    @Query(value = "delete from repertorio_evento where id_evento = :idEvento and tipo_evento = :tipoEvento", nativeQuery = true)
    void limparRepertorioEvento(Long idEvento, String tipoEvento);

    @Query(value = "SELECT r FROM RepertorioEvento r WHERE r.id.idEvento = :idEvento and r.id.tipoEvento = :tipoEvento")
    List<RepertorioEvento> buscarPorEvento(Long idEvento, TipoEvento tipoEvento);

    @Query(value = "SELECT r FROM RepertorioEvento r WHERE r.id.idEvento = :idEvento and r.id.tipoEvento = :tipoEvento and r.id.indice = :indice")
    RepertorioEvento buscarPorIndiceEEvento(Integer indice, Long idEvento, TipoEvento tipoEvento);

    /**
     * Índice, título, artista e duração das faixas de um evento, em ordem.
     *
     * <p>Usada pelo Modo Performance para congelar o repertório no estado da sessão. Projeção
     * crua em vez de {@link #buscarPorEvento}: carregar as entidades inteiras traria a coluna
     * {@code letra} (TEXT) de cada música, que o servidor da sessão nunca usa.
     */
    @Query(value = "select re.indice, re.titulo, re.artista, re.duracao "
            + "from repertorio_evento re "
            + "where re.id_evento = :idEvento and re.tipo_evento = :tipoEvento "
            + "order by re.indice", nativeQuery = true)
    List<Object[]> buscarFaixasParaSessaoAoVivo(Long idEvento, String tipoEvento);
}
