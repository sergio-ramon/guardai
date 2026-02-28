package com.ramon.guardai.application;

import com.ramon.guardai.infra.DBConnect;
import com.ramon.guardai.model.TaxaMensal;
import com.ramon.guardai.service.BcbService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        DBConnect dbConnect = new DBConnect();

        // URI de acesso à API do banco central
        String url = "https://api.bcb.gov.br/dados/serie/bcdata.sgs.12/dados?formato=json&dataInicial=03/02/2026&dataFinal=03/02/2026";

        // Pega o valor da taxa DI mensal
        BigDecimal taxaDiMensal = BcbService.pegaTaxaDiMensal(url);
        // Cria um objeto TaxaMensal com o valor da taxa DI mensal e adiciona ao banco de dados
        TaxaMensal taxaMensal = new TaxaMensal(0, "Taxa DI", taxaDiMensal);
        // Adiciona a taxa mensal ao banco de dados
        dbConnect.adicionarTaxa(taxaMensal);

        // Pega o valor do aporte e converte em BigDecimal
        double aporte;
        do {
            System.out.print("Digite o valor do aporte mensal: ");
            aporte = input.nextDouble();
            input.nextLine();
        } while (aporte < 0);
        BigDecimal aporteBigDecimal = new BigDecimal(aporte);

        // Pega o valor do período em meses
        int meses;
        do {
            System.out.print("Digite o período em meses: ");
            meses = input.nextInt();
            input.nextLine();
        } while (meses <= 0);

        // Calcula o valor acumulado com rendimentos no mês
        BigDecimal valorTotalAcumulado = BigDecimal.ZERO; // Valor acumulado inicia em zero
        BigDecimal valorTaxa = dbConnect.pegarTaxa(); // Pega a última taxa DI inserida no banco de dados
        if (valorTaxa != null) {
             valorTotalAcumulado = calcularDesempenho(aporteBigDecimal, meses, valorTaxa);
        }

        // Valor total aportado do próprio bolso somado
        BigDecimal aporteTotal = new BigDecimal(aporte * meses);
        // Rendimento composto total sobre os valores mensais
        BigDecimal rendimento = valorTotalAcumulado.subtract(aporteTotal);
        // Número 100 em BigDecimal
        BigDecimal hundredBigDecimal = new BigDecimal("100");
        // Desempenho do investimento em percentagem
        BigDecimal desempenho = rendimento
                .divide(aporteTotal, RoundingMode.HALF_UP)
                .multiply(hundredBigDecimal)
                .setScale(2, RoundingMode.HALF_UP);

        System.out.println();
        System.out.println("Valor aplicado do bolso: R$ " + aporteTotal
                .setScale(2, RoundingMode.HALF_UP));
        System.out.println("Valor do rendimento: R$ " + rendimento.setScale(2, RoundingMode.HALF_UP));
        System.out.println("Valor acumulado: R$ " + valorTotalAcumulado
                .setScale(2, RoundingMode.HALF_UP));
        System.out.println("Performance: " + desempenho + "%");
    }

    private static BigDecimal calcularDesempenho(BigDecimal aporte, int meses, BigDecimal taxa) {
        // Inicializa o valor acumulado em 0
        BigDecimal valorInvestimentoAcumulado = BigDecimal.ZERO;

        // Repete conforme a quantidade de meses
        for (int i = 0; i < meses; i++) {
            // Adiciona o valor da aplicação do mês ao acumulado
            valorInvestimentoAcumulado = valorInvestimentoAcumulado.add(aporte);
            // Adiciona o valor do rendimento do mês ao acumulado
            valorInvestimentoAcumulado = valorInvestimentoAcumulado.add(taxa.multiply(valorInvestimentoAcumulado));
        }

        // Retorna o valor acumulado total
        return valorInvestimentoAcumulado;
    }
}
