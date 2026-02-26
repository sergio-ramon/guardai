package com.ramon.guardai.infra;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;

public class DBConnect {
    // Variáveis de instância para armazenar as propriedades de conexão
    private final String url;
    private final String user;
    private final String password;

    public DBConnect() {
        // Carrega as propriedades do arquivo db.properties
        Properties propriedades = new Properties();
        // O caminho do arquivo db.properties é relativo ao diretório raiz do projeto
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/db.properties")) {
            propriedades.load(inputStream);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        // Atribui os valores das propriedades às variáveis de instância
        this.url = propriedades.getProperty("DB_URL");
        this.user = propriedades.getProperty("DB_USER");
        this.password = propriedades.getProperty("DB_PASSWORD");
    }

    private Connection conectar() throws SQLException {
        // Estabelece a conexão com o banco de dados usando as propriedades carregadas
        return DriverManager.getConnection(url, user, password);
    }

    public void adicionarTaxa(BigDecimal valorTaxa) {
        String sql = "INSERT INTO taxa_mensal (nome_taxa, valor_taxa) VALUES (?, ?)";

        try (Connection conexao = conectar();
             PreparedStatement statement = conexao.prepareStatement(sql)) {

            statement.setString(1, "Taxa DI");
            statement.setBigDecimal(2, valorTaxa);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public BigDecimal pegarTaxa() {
        String sql = "SELECT valor_taxa FROM taxa_mensal " +
                "WHERE nome_taxa = 'Taxa DI' " +
                "ORDER BY id DESC LIMIT 1";

        try (Connection conexao = conectar();
            PreparedStatement statement = conexao.prepareStatement(sql)) {

            ResultSet set = statement.executeQuery();

            BigDecimal valorTaxa = BigDecimal.ZERO;
            while(set.next()) {
                valorTaxa = set.getBigDecimal("valor_taxa");
            }

            return valorTaxa;

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
