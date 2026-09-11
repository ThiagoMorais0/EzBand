package com.baseapplication.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Catalogo dos itens tecnicos. O tipo e canonico (permite somar tomadas, contar
 * vias e numerar canais); marca, modelo e observacao ficam livres ao lado.
 * OUTRO cobre o que fugir da lista sem engessar o usuario.
 *
 * canaisPadrao e apenas a sugestao inicial: o valor final vive no item e pode
 * ser editado (bateria costuma valer 6, teclado estereo 2, e um microfone que
 * so capta um amplificador ja contado vale 0).
 */
@Getter
public enum TipoItemPalco {

    // --- Fonte de som ---
    // Bateria, percussao e teclado sao tipos proprios (e nao INSTRUMENTO com o
    // nome escrito) porque cada um tem um simbolo distinto em planta e um
    // tamanho de ocupacao proprio no palco.
    BATERIA(CategoriaItemPalco.FONTE_SOM, "Bateria", "bateria", true, 6,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    PERCUSSAO(CategoriaItemPalco.FONTE_SOM, "Percussão", "percussao", true, 2,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    TECLADO(CategoriaItemPalco.FONTE_SOM, "Teclado", "teclado", true, 2,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    INSTRUMENTO(CategoriaItemPalco.FONTE_SOM, "Instrumento", "instrumento", false, 1,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    AMPLIFICADOR(CategoriaItemPalco.FONTE_SOM, "Amplificador", "amplificador", true, 1,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    PEDALEIRA(CategoriaItemPalco.FONTE_SOM, "Pedaleira", "pedaleira", false, 1,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    PEDAL(CategoriaItemPalco.FONTE_SOM, "Pedal", "pedal", false, 0,
            EnumSet.of(CampoItemPalco.MARCA_MODELO)),
    DI(CategoriaItemPalco.FONTE_SOM, "Direct box (DI)", "di", false, 1,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    MICROFONE(CategoriaItemPalco.FONTE_SOM, "Microfone", "microfone", false, 1,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    SUPORTE(CategoriaItemPalco.FONTE_SOM, "Suporte / pedestal", "suporte", false, 0,
            EnumSet.of(CampoItemPalco.MARCA_MODELO)),

    // --- Monitoracao ---
    MONITOR_WEDGE(CategoriaItemPalco.MONITORACAO, "Monitor de chão (wedge)", "wedge", true, 0,
            EnumSet.of(CampoItemPalco.VIAS, CampoItemPalco.MIX_INDEPENDENTE, CampoItemPalco.MARCA_MODELO)),
    IN_EAR(CategoriaItemPalco.MONITORACAO, "In-ear", "inear", false, 0,
            EnumSet.of(CampoItemPalco.VIAS, CampoItemPalco.MIX_INDEPENDENTE, CampoItemPalco.MARCA_MODELO)),
    SIDEFILL(CategoriaItemPalco.MONITORACAO, "Sidefill", "sidefill", true, 0,
            EnumSet.of(CampoItemPalco.VIAS, CampoItemPalco.MARCA_MODELO)),

    // --- Energia ---
    TOMADA(CategoriaItemPalco.ENERGIA, "Tomada", "tomada", true, 0,
            EnumSet.of(CampoItemPalco.VOLTAGEM, CampoItemPalco.PONTO)),
    EXTENSAO_REGUA(CategoriaItemPalco.ENERGIA, "Extensão / régua", "extensao", false, 0,
            EnumSet.of(CampoItemPalco.PONTO, CampoItemPalco.MARCA_MODELO)),

    // --- Estrutura ---
    PRATICAVEL(CategoriaItemPalco.ESTRUTURA, "Praticável / riser", "praticavel", true, 0,
            EnumSet.of(CampoItemPalco.ROTULO, CampoItemPalco.MARCA_MODELO)),
    MESA_SOM(CategoriaItemPalco.ESTRUTURA, "Mesa de som", "mesa", true, 0,
            EnumSet.of(CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS)),
    PA(CategoriaItemPalco.ESTRUTURA, "Sistema de PA", "pa", true, 0,
            EnumSet.of(CampoItemPalco.MARCA_MODELO)),
    ILUMINACAO(CategoriaItemPalco.ESTRUTURA, "Iluminação", "iluminacao", false, 0,
            EnumSet.of(CampoItemPalco.ROTULO, CampoItemPalco.MARCA_MODELO)),
    OUTRO(CategoriaItemPalco.ESTRUTURA, "Outro", "outro", true, 0,
            EnumSet.of(CampoItemPalco.ROTULO, CampoItemPalco.MARCA_MODELO, CampoItemPalco.CANAIS));

    private final CategoriaItemPalco categoria;
    private final String descricao;
    /** Chave do icone; o front mapeia para o SVG correspondente. */
    private final String icone;
    /** Se aparece desenhado no palco (derivado da posicao ou como objeto solto). */
    private final Boolean desenhavel;
    private final Integer canaisPadrao;
    private final Set<CampoItemPalco> campos;

    TipoItemPalco(CategoriaItemPalco categoria, String descricao, String icone,
                  Boolean desenhavel, Integer canaisPadrao, Set<CampoItemPalco> campos) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.icone = icone;
        this.desenhavel = desenhavel;
        this.canaisPadrao = canaisPadrao;
        this.campos = campos;
    }

    public boolean temCampo(CampoItemPalco campo) {
        return campos.contains(campo);
    }

    public static List<TipoItemPalco> getAll() {
        return Arrays.asList(TipoItemPalco.values());
    }
}
