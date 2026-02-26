package com.ramon.guardai.model;

import java.math.BigDecimal;

public class TaxaMensal {
    // Identificador único para cadastro junto ao banco de dados
    private final int id;
    // Exemplo: "Taxa DI"
    private String nome;
    // Atribuição direta do valor da taxa mensal obtida via BcbService
    private BigDecimal valorTaxa;

    public TaxaMensal(int id, String nome, BigDecimal valorTaxa) {
        this.id = id;
        this.nome = nome;
        this.valorTaxa = valorTaxa;
    }

    public int getId() { return this.id; }

    public void setNome(String nome) { this.nome = nome; }
    public String getNome() { return this.nome; }
    public void setValorTaxa(BigDecimal valorTaxa) { this.valorTaxa = valorTaxa; }
    public BigDecimal getValorTaxa() { return this.valorTaxa; }
}
