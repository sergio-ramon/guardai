package com.ramon.guardai.model;

import java.math.BigDecimal;

public record TaxaMensal(int id, String nome, BigDecimal valorTaxa) {
    public TaxaMensal {
        if (id < 0) {
            throw new IllegalArgumentException("ID deve ser um número inteiro positivo.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da taxa não pode ser nulo ou vazio.");
        }
        if (valorTaxa == null || valorTaxa.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor da taxa deve ser um número decimal positivo.");
        }
    }
}
