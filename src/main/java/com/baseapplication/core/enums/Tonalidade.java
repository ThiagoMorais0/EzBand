package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum Tonalidade {
    ORIGINAL("0"),
    MEIO_TOM_ABAIXO("-1"),
    UM_TOM_ABAIXO("-2"),
    UM_TOM_E_MEIO_ABAIXO("-3"),
    DOIS_TONS_ABAIXO("-4"),
    DOIS_TONS_E_MEIO_ABAIXO("-5"),
    TRES_TONS_ABAIXO("-6"),
    TRES_TONS_E_MEIO_ABAIXO("-7"),
    QUATRO_TONS_ABAIXO("-8"),
    QUATRO_TONS_E_MEIO_ABAIXO("-9"),
    CINCO_TONS_ABAIXO("-10"),
    CINCO_TONS_E_MEIO_ABAIXO("-11"),
    SEIS_TONS_ABAIXO("-12"),
    SEIS_TONS_E_MEIO_ABAIXO("-13"),
    SETE_TONS_ABAIXO("-14"),
    SETE_TONS_E_MEIO_ABAIXO("-15"),
    OITO_TONS_ABAIXO("-16"),
    MEIO_TOM_ACIMA("1"),
    UM_TOM_ACIMA("2"),
    UM_TOM_E_MEIO_ACIMA("3"),
    DOIS_TONS_ACIMA("4"),
    DOIS_TONS_E_MEIO_ACIMA("5"),
    TRES_TONS_ACIMA("6"),
    TRES_TONS_E_MEIO_ACIMA("7"),
    QUATRO_TONS_ACIMA("8"),
    QUATRO_TONS_E_MEIO_ACIMA("9"),
    CINCO_TONS_ACIMA("10"),
    CINCO_TONS_E_MEIO_ACIMA("11"),
    SEIS_TONS_ACIMA("12"),
    SEIS_TONS_E_MEIO_ACIMA("13"),
    SETE_TONS_ACIMA("14"),
    SETE_TONS_E_MEIO_ACIMA("15"),
    OITO_TONS_ACIMA("16"),
    OITO_TONS_E_MEIO_ACIMA("17");

    private final String numero;

    Tonalidade(String numero){
        this.numero = numero;
    }

    public static Tonalidade encontrarPeloNumero(String numero) {
        for (Tonalidade tonalidade : Tonalidade.values()) {
            if (tonalidade.getNumero().equals(numero)) {
                return tonalidade;
            }
        }
        throw new IllegalArgumentException("Tonalidade não encontrada para o número: " + numero);
    }
}
