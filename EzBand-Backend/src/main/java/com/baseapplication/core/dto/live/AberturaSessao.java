package com.baseapplication.core.dto.live;

/**
 * Resultado de {@code abrirOuEntrar}: o snapshot, e se foi ESTA chamada que criou a sessão.
 *
 * <p>Só quando {@code recemCriada} é verdadeiro é que existe algo a notificar — todo mundo
 * que só está entrando numa sessão já em andamento não deve gerar push nenhum.
 */
public record AberturaSessao(LiveSessionSnapshot snapshot, boolean recemCriada) {
}
