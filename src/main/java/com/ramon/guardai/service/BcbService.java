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

    public static BigDecimal getMonthlyDIInterest(String url) {
        // Cliente HTTP que fará a requisição
        try (HttpClient client = HttpClient.newHttpClient()) {

            // Requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            // Resposta HTTP recebida por uma Thread separada
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // Conversão do Json da resposta para SerialData
            List<SerialData> responseBody = mapper.readValue(response.body(), new TypeReference<>() {});
            // Coleta o valor da taxa diária no atributo value
            BigDecimal value = responseBody.getFirst().getValue();

            // Calcula a taxa mensal
            return calculateMonthlyInterest(value);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static BigDecimal calculateMonthlyInterest(BigDecimal dataValue) {
        // Define uma variável com o valor 100 em BigDecimal
        BigDecimal hundred = new BigDecimal("100");

        // Pega o valor da taxa diária dividida por 100
        BigDecimal dailyInterest = dataValue.divide(hundred, RoundingMode.HALF_UP);
        // Adiciona 1 ao valor da taxa diária
        dailyInterest = dailyInterest.add(BigDecimal.ONE);
        // Eleva a taxa diária a 21 (período mensal de dias úteis)
        dailyInterest = dailyInterest.pow(21);
        // Subtrai 1 do valor da taxa diári
        dailyInterest = dailyInterest.subtract(BigDecimal.ONE);

        // Retorna o valor da taxa mensal arredondado com 4 casas decimais
        return dailyInterest.setScale(4, RoundingMode.HALF_UP);
    }
}
