package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.SessaoAoVivoDao;
import com.baseapplication.core.dto.live.EdicaoResumoDTO;
import com.baseapplication.core.dto.live.ResumoSessaoDTO;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.SessaoAoVivo;
import com.baseapplication.core.model.SessaoAoVivoFaixa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SessaoAoVivoServiceImpl - correção do resumo pós-show")
class SessaoAoVivoServiceImplEditarResumoTest {

    private static final Long ID_SESSAO = 7L;
    private static final Long ID_BANDA = 3L;
    private static final Long ID_ADMIN = 42L;
    private static final Long ID_MEMBRO = 99L;

    @Mock
    private SessaoAoVivoDao sessaoAoVivoDao;

    @Mock
    private MusicoBandaDao musicoBandaDao;

    @Mock
    private com.baseapplication.core.dao.UsuarioDao usuarioDao;

    private SessaoAoVivoServiceImpl service;
    private SessaoAoVivo sessao;

    @BeforeEach
    void preparar() {
        service = new SessaoAoVivoServiceImpl(sessaoAoVivoDao, musicoBandaDao, usuarioDao);
        when(usuarioDao.findById(any())).thenReturn(Optional.empty());

        sessao = new SessaoAoVivo();
        sessao.setId(ID_SESSAO);
        sessao.setIdBanda(ID_BANDA);
        sessao.setIniciadaEm(LocalDateTime.of(2026, 3, 1, 22, 0));
        sessao.setFinalizadaEm(LocalDateTime.of(2026, 3, 2, 0, 0));
        sessao.setTotalFaixasRepertorio(3);

        sessao.adicionarFaixa(faixa(1L, 0, "Black Dog", "Led Zeppelin", 300, 280));
        sessao.adicionarFaixa(faixa(2L, 1, "Kashmir", "Led Zeppelin", 500, 480));
        // A repetição acidental: mesmo índice, aberta de novo por um clique errado.
        sessao.adicionarFaixa(faixa(3L, 2, "Black Dog", "Led Zeppelin", 30, 280));

        when(sessaoAoVivoDao.findById(ID_SESSAO)).thenReturn(Optional.of(sessao));
        when(sessaoAoVivoDao.save(any(SessaoAoVivo.class))).thenAnswer(inv -> inv.getArgument(0));
        darPermissao(ID_ADMIN, PermissaoMusico.ADMINISTRADOR);
        darPermissao(ID_MEMBRO, PermissaoMusico.MEMBRO_REGULAR);
    }

    @Test
    @DisplayName("remover a repetição acidental apaga a linha e recalcula o desvio")
    void removerRepeticao() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 300), linha(2L, "Kashmir", 500));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals(2, resumo.getFaixasTocadas());
        // (300-280) + (500-480) = 40. A linha de 30s contra 280s cadastrados saiu do cálculo.
        assertEquals(40, resumo.getDesvioSegundos());
    }

    @Test
    @DisplayName("a ordem da lista recebida vira o campo ordem, sem mexer em iniciadaEm")
    void reordenar() {
        LocalDateTime inicioOriginalDoKashmir = sessao.getFaixas().get(1).getIniciadaEm();
        EdicaoResumoDTO edicao = edicaoCom(linha(2L, "Kashmir", 500), linha(1L, "Black Dog", 300));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals("Kashmir", resumo.getFaixas().get(0).getTitulo());
        assertEquals("Black Dog", resumo.getFaixas().get(1).getTitulo());
        assertEquals(0, resumo.getFaixas().get(0).getOrdem());
        assertEquals(1, resumo.getFaixas().get(1).getOrdem());
        assertEquals(inicioOriginalDoKashmir, resumo.getFaixas().get(0).getIniciadaEm());
    }

    @Test
    @DisplayName("linha sem id vira faixa manual e fica fora do desvio")
    void adicionarFaixaEsquecida() {
        EdicaoResumoDTO edicao = edicaoCom(
                linha(1L, "Black Dog", 300),
                linha(2L, "Kashmir", 500),
                linha(null, "Whole Lotta Love", 240));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals(3, resumo.getFaixasTocadas());
        ResumoSessaoDTO.FaixaDTO nova = resumo.getFaixas().get(2);
        assertEquals("Whole Lotta Love", nova.getTitulo());
        assertTrue(nova.getAdicionadaManualmente());
        assertNull(nova.getIniciadaEm());
        // Continua 40: a faixa manual não tem duração cadastrada e não entra na conta.
        assertEquals(40, resumo.getDesvioSegundos());
    }

    @Test
    @DisplayName("corrigir a duração real de uma faixa muda o desvio")
    void corrigirDuracao() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 280), linha(2L, "Kashmir", 480));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals(0, resumo.getDesvioSegundos());
    }

    @Test
    @DisplayName("a duração total do show não muda: a banda ficou o mesmo tempo no palco")
    void duracaoTotalIntacta() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 300));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals(2 * 60 * 60, resumo.getDuracaoTotalSegundos());
    }

    @Test
    @DisplayName("admin recebe podeEditar verdadeiro e membro regular não")
    void sinalizaPermissaoNoDto() {
        ResumoSessaoDTO comoAdmin = service.editarResumo(
                ID_SESSAO, edicaoCom(linha(1L, "Black Dog", 300)), ID_ADMIN);

        assertTrue(comoAdmin.getPodeEditar());
    }

    @Test
    @DisplayName("a edição deixa rastro de quem corrigiu e quando")
    void registraRastro() {
        service.editarResumo(ID_SESSAO, edicaoCom(linha(1L, "Black Dog", 300)), ID_ADMIN);

        assertEquals(ID_ADMIN, sessao.getEditadoPor());
        assertNotNull(sessao.getEditadoEm());
    }

    @Test
    @DisplayName("membro regular não corrige resumo e nada é gravado")
    void membroRegularNaoPode() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 300));

        assertThrows(InternalException.class, () -> service.editarResumo(ID_SESSAO, edicao, ID_MEMBRO));
        verify(sessaoAoVivoDao, never()).save(any(SessaoAoVivo.class));
    }

    @Test
    @DisplayName("quem não é da banda não corrige resumo")
    void estranhoNaoPode() {
        when(musicoBandaDao.findById(any())).thenReturn(Optional.empty());
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 300));

        assertThrows(InternalException.class, () -> service.editarResumo(ID_SESSAO, edicao, 1234L));
        verify(sessaoAoVivoDao, never()).save(any(SessaoAoVivo.class));
    }

    @Test
    @DisplayName("fundador corrige resumo mesmo sem o papel de administrador")
    void fundadorPode() {
        darPermissao(ID_MEMBRO, PermissaoMusico.FUNDADOR);

        ResumoSessaoDTO resumo = service.editarResumo(
                ID_SESSAO, edicaoCom(linha(1L, "Black Dog", 300)), ID_MEMBRO);

        assertEquals(1, resumo.getFaixasTocadas());
    }

    @Test
    @DisplayName("linha sem título é descartada em vez de gravar faixa fantasma")
    void linhaSemTitulo() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", 300), linha(null, "   ", 100));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertEquals(1, resumo.getFaixasTocadas());
    }

    @Test
    @DisplayName("duração negativa vira nula em vez de contaminar o desvio")
    void duracaoNegativa() {
        EdicaoResumoDTO edicao = edicaoCom(linha(1L, "Black Dog", -5));

        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, edicao, ID_ADMIN);

        assertNull(resumo.getFaixas().get(0).getDuracaoRealSegundos());
        assertEquals(0, resumo.getDesvioSegundos());
    }

    @Test
    @DisplayName("lista vazia esvazia o resumo sem estourar")
    void listaVazia() {
        ResumoSessaoDTO resumo = service.editarResumo(ID_SESSAO, new EdicaoResumoDTO(), ID_ADMIN);

        assertEquals(0, resumo.getFaixasTocadas());
        assertFalse(resumo.getFaixas().stream().findAny().isPresent());
    }

    // ── helpers ─────────────────────────────────────────────────────────

    private void darPermissao(Long idUsuario, PermissaoMusico permissao) {
        MusicoBanda membro = new MusicoBanda();
        membro.setPermissoes(new ArrayList<>(List.of(permissao)));
        when(musicoBandaDao.findById(argThatIdentifica(idUsuario))).thenReturn(Optional.of(membro));
    }

    private com.baseapplication.core.model.MusicoBandaId argThatIdentifica(Long idUsuario) {
        return new com.baseapplication.core.model.MusicoBandaId(idUsuario, ID_BANDA);
    }

    private SessaoAoVivoFaixa faixa(Long id, int ordem, String titulo, String artista,
                                    Integer real, Integer cadastrada) {
        SessaoAoVivoFaixa f = new SessaoAoVivoFaixa();
        f.setId(id);
        f.setOrdem(ordem);
        f.setIndice(ordem);
        f.setTitulo(titulo);
        f.setArtista(artista);
        f.setIniciadaEm(LocalDateTime.of(2026, 3, 1, 22, 0).plusMinutes(ordem * 5L));
        f.setDuracaoRealSegundos(real);
        f.setDuracaoCadastradaSegundos(cadastrada);
        f.setAdicionadaManualmente(false);
        return f;
    }

    private EdicaoResumoDTO.FaixaEditadaDTO linha(Long id, String titulo, Integer duracaoReal) {
        EdicaoResumoDTO.FaixaEditadaDTO l = new EdicaoResumoDTO.FaixaEditadaDTO();
        l.setId(id);
        l.setTitulo(titulo);
        l.setArtista("Led Zeppelin");
        l.setDuracaoRealSegundos(duracaoReal);
        return l;
    }

    private EdicaoResumoDTO edicaoCom(EdicaoResumoDTO.FaixaEditadaDTO... linhas) {
        EdicaoResumoDTO edicao = new EdicaoResumoDTO();
        edicao.setFaixas(new ArrayList<>(List.of(linhas)));
        return edicao;
    }
}
