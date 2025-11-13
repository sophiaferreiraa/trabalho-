package DAO_bancoDados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//CLASSE PADRAO CONEXAO BD
// deve ser adcionada aq(Connection conexao = ConnectionFactory.getConnection();)
public class ClasseConexaoBD {

    private static final String URL = "jdbc:mysql://localhost:3306/meubanco";

    // Usuário e senha do banco
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Método estático que cria e devolve uma nova conexão pronta para uso
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco", e);
        }
    }
}
