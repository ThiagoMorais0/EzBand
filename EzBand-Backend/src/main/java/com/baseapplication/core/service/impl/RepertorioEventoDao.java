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
     * Itens de repertorio de varios eventos de uma banda de uma vez.
     *
     * <p>Serve a propagacao de uma edicao do setlist da banda para os shows pendentes. A musica e
     * reencontrada pela chave normalizada de titulo + artista, que so existe em Java (ver
     * {@link com.baseapplication.core.utils.ChaveMusica}), entao o filtro por musica acontece na
     * memoria -- e por isso esta consulta traz os itens de todos os shows de uma vez, em vez de
     * uma ida ao banco por show.
     */
    @Query(value = "SELECT r FROM RepertorioEvento r WHERE r.id.idBanda = :idBanda "
            + "and r.id.tipoEvento = :tipoEvento and r.id.idEvento in :idsEventos")
    List<RepertorioEvento> buscarPorEventos(Long idBanda, TipoEvento tipoEvento, List<Long> idsEventos);

    /**
     * Índice, título, artista e duração das faixas de um evento, em ordem.
     *
     * <p>Usada pelo Modo Performance para congelar o repertório no estado da sessão. Projeção
     * crua em vez de {@link #buscarPorEvento}: carregar as entidades inteiras traria a coluna
     * {@code letra} (TEXT) de cada música, que o servidor da sessão nunca usa.
     *
     * <p>Momentos ficam de fora, e é aqui que essa decisão mora. O ponteiro da sessão conta
     * músicas — a banda fala "vai pra 7", não "vai pra nona linha" —, então deixar um "dar boa
     * noite" virar faixa deslocaria a numeração, o log de tocadas e o resumo pós-show de uma
     * vez só. Como {@code carregarFaixas} reindexa por posição na lista, filtrar aqui não abre
     * buraco nenhum: o momento simplesmente não existe para o servidor da sessão.
     */
    @Query(value = "select re.indice, re.titulo, re.artista, re.duracao "
            + "from repertorio_evento re "
            + "where re.id_evento = :idEvento and re.tipo_evento = :tipoEvento "
            + "and re.tipo_item = 'MUSICA' "
            + "order by re.indice", nativeQuery = true)
    List<Object[]> buscarFaixasParaSessaoAoVivo(Long idEvento, String tipoEvento);
}
