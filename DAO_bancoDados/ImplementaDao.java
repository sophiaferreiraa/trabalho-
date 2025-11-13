package DAO_bancoDados;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImplementaDao implements InterfaceDao{

    // ----------- IMPLEMENTAÇÃO DO SINGLETON -------------

    // 1. Campo estático para guardar a instância única
    private static ImplementaDao instancia;

    // 2. Construtor PRIVADO (ninguém de fora pode criar)
    private ImplementaDao() {
        // Construtor vazio para impedir instanciação
    }

    // 3. Método de acesso global (o único jeito de pegar o DAO)
    public static synchronized ImplementaDao getInstance() {
        // Se for a primeira vez, cria o objeto
        if (instancia == null) {
            instancia = new ImplementaDao();
        }
        // Retorna a instância única
        return instancia;
    }
    // -------------- FIM DO SINGLETON ---------------------------

  // MÉTODO: salvar — insere um novo contato na tabela 'contatos'
    @Override // Indica que este método está sobrescrevendo um método da interface
    public void salva(Contato contato) throws DAOException {
        // String SQL com placeholders (?) para evitar concatenação e SQL injection
        String sql = "INSERT INTO contatos(nome, email) VALUES (?, ?)";

        // try-with-resources: abre a Conexão E o PreparedStatement
        // e garante que ambos serão fechados automaticamente no final do bloco
        try (Connection connection = ClasseConexaoBD.getConnection(); // Obtém uma nova conexão com o banco
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Preenche o primeiro placeholder (?) com o nome do objeto Contato
            stmt.setString(1, contato.getNome());
            // Preenche o segundo placeholder (?) com o email do objeto Contato
            stmt.setString(2, contato.getEmail());

            // Executa o comando INSERT no banco (executeUpdate é usado para INSERT/UPDATE/DELETE)
            stmt.executeUpdate();

            // Depois do INSERT, pedimos as chaves geradas (ex.: id autoincrement)
            try (ResultSet rs = stmt.getGeneratedKeys()) { // Obtém o resultado com as chaves
                // rs.next() avança para o primeiro resultado; se existir, pegamos o valor
                if (rs.next()) {
                    // Define o id retornado pelo banco no objeto contato (sincroniza objeto Java com DB)
                    contato.setId(rs.getInt(1)); // coluna 1 do ResultSet (a chave gerada)
                }
            }

        } catch (SQLException e) { // Captura um erro de SQL se algo falhar
            // Se ocorrer qualquer erro de banco, embrulha em uma exceção do nível DAO
            // Isso evita vazar detalhes de SQL para camadas superiores e centraliza tratamento
            throw new DAOException("Erro ao salvar contato", e);
        }
    } // A Conexão e o PreparedStatement são fechados aqui automaticamente

    // MÉTODO: buscarPorId — busca um contato específico pelo id (SELECT ... WHERE id = ?)
    @Override // Sobrescreve o método da interface
    public Contato buscarPorID(int id) throws DAOException {
        // SQL que seleciona apenas as colunas necessárias e filtra por id
        String sql = "SELECT id, nome, email FROM contatos WHERE id = ?";

        // try-with-resources: abre a Conexão e o PreparedStatement
        try (Connection connection = ClasseConexaoBD.getConnection(); // Obtém uma nova conexão
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Substitui o placeholder (?) pelo valor do id recebido no parâmetro
            stmt.setInt(1, id);

            // Executa a consulta e obtém um ResultSet (conjunto de linhas retornadas)
            try (ResultSet rs = stmt.executeQuery()) { // O ResultSet também é fechado automaticamente
                // Se houver uma linha (rs.next() == true), cria e retorna um objeto Contato com os dados
                if (rs.next()) {
                    // Cria e retorna um novo objeto Contato
                    return new Contato(
                        rs.getInt("id"),          // pega a coluna "id" do resultado
                        rs.getString("nome"),     // pega a coluna "nome"
                        rs.getString("email")     // pega a coluna "email"
                    );
                }
                // Se não encontrou registro com aquele id, retorna null (significa "não encontrado")
                return null;
            }

        } catch (SQLException e) { // Captura erros de SQL
            // Em caso de erro, lança DAOException com mensagem clara e a causa original
            throw new DAOException("Erro ao buscar contato", e);
        }
    } // Conexão, Statement e ResultSet são fechados aqui

    // MÉTODO: buscarTodos — retorna uma lista com todos os contatos da tabela
    @Override // Sobrescreve o método da interface
    public List<Contato> buscarTodos() throws DAOException {
        // SQL simples para listar todas as linhas/colunas desejadas
        String sql = "SELECT id, nome, email FROM contatos";
        // Cria a lista que será preenchida com os objetos Contato lidos do ResultSet
        List<Contato> lista = new ArrayList<>();

        // try-with-resources com Connection, PreparedStatement e ResultSet — todos serão fechados automaticamente
        try (Connection connection = ClasseConexaoBD.getConnection(); // Obtém uma nova conexão
             PreparedStatement stmt = connection.prepareStatement(sql); // Prepara o SQL
             ResultSet rs = stmt.executeQuery()) { // Executa a busca e obtém os resultados

            // Itera por cada linha do resultado enquanto houver próximo
            while (rs.next()) {
                // Para cada linha, cria um novo Contato com os valores das colunas e adiciona à lista
                lista.add(new Contato(
                    rs.getInt("id"), // Pega o id da linha atual
                    rs.getString("nome"), // Pega o nome da linha atual
                    rs.getString("email") // Pega o email da linha atual
                ));
            }

            // Retorna a lista preenchida (pode ser vazia se não houver registros)
            return lista;

        } catch (SQLException e) { // Captura erros de SQL
            // Em caso de erro, transforma em DAOException
            throw new DAOException("Erro ao listar contatos", e);
        }
    } // Conexão, Statement e ResultSet são fechados aqui

    // MÉTODO: atualizar — atualiza nome e email de um contato existente (WHERE id = ?)
    @Override // Sobrescreve o método da interface
    public void atualizar(Contato contato) throws DAOException {
        // SQL de atualização com placeholders para nome, email e id
        String sql = "UPDATE contatos SET nome = ?, email = ? WHERE id = ?";

        // Tenta preparar e executar o statement; Connection e PreparedStatement serão fechados automaticamente
        try (Connection connection = ClasseConexaoBD.getConnection(); // Obtém uma nova conexão
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Preenche o primeiro ? com o novo nome
            stmt.setString(1, contato.getNome());
            // Preenche o segundo ? com o novo email
            stmt.setString(2, contato.getEmail());
            // Preenche o terceiro ? com o id que identifica qual linha atualizar
            stmt.setInt(3, contato.getId());

            // Executa o UPDATE no banco
            stmt.executeUpdate();

        } catch (SQLException e) { // Captura erros de SQL
            // Envolve em DAOException para a camada superior saber que deu erro na camada de persistência
            throw new DAOException("Erro ao atualizar contato", e);
        }
    } // Conexão e Statement são fechados aqui

    // MÉTODO: deletar — remove o registro do contato com o id informado
    @Override // Sobrescreve o método da interface
    public void deletar(int id) throws DAOException {
        // SQL para deletar uma linha pelo id
        String sql = "DELETE FROM contatos WHERE id = ?";

        // try-with-resources abre a Connection e o PreparedStatement
        try (Connection connection = ClasseConexaoBD.getConnection(); // Obtém uma nova conexão
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Define o parâmetro id
            stmt.setInt(1, id);
            // Executa o DELETE
            stmt.executeUpdate();

        } catch (SQLException e) { // Captura erros de SQL
            // Lança DAOException em caso de erro
            throw new DAOException("Erro ao deletar contato", e);
        }
    } // Conexão e Statement são fechados aqui
}
