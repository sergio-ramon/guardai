package com.ramon.guardai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.ramon.guardai.model.SerialData;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class BcbService {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static BigDecimal pegaTaxaDiMensal(String url) {
        // Cliente HTTP que fará a requisição
        try (HttpClient cliente = HttpClient.newHttpClient()) {

            // Requisição HTTP
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            // Resposta HTTP recebida por uma Thread separada
            HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
            // Conversão do Json da resposta para SerialData
            List<SerialData> respostaBody = mapper.readValue(resposta.body(), new TypeReference<>() {});
            // Coleta o valor da taxa diária no atributo value
            BigDecimal value = respostaBody.getFirst().getValue();

            // Calcula a taxa mensal
            return calculaTaxaMensal(value);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static BigDecimal calculaTaxaMensal(BigDecimal valorDado) {
        // Define uma variável com o valor 100 em BigDecimal
        BigDecimal hundred = new BigDecimal("100");

        // Pega o valor da taxa diária dividida por 100
        BigDecimal taxaDiaria = valorDado.divide(hundred, RoundingMode.HALF_UP);
        // Adiciona 1 ao valor da taxa diária
        taxaDiaria = taxaDiaria.add(BigDecimal.ONE);
        // Eleva a taxa diária a 21 (período mensal de dias úteis)
        taxaDiaria = taxaDiaria.pow(21);
        // Subtrai 1 do valor da taxa diári
        taxaDiaria = taxaDiaria.subtract(BigDecimal.ONE);

        // Retorna o valor da taxa mensal arredondado com 4 casas decimais
        return taxaDiaria.setScale(4, RoundingMode.HALF_UP);
    }
}
