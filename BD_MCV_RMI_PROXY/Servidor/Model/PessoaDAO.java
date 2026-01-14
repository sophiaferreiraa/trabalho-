package BD_MCV_RMI_PROXY.Servidor.Model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PessoaDAO extends UnicastRemoteObject implements PessoaInterface{

    public PessoaDAO() throws RemoteException {
        super();
    }

    public void cadastrar(int id, String nome, String escola) throws RemoteException{
        String sql = "INSERT INTO pessoa(id, nome, escola) VALUES(?, ?, ?)";

        try (Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.setString(2, nome);
            stmt.setString(3, escola);
            stmt.executeUpdate();

            System.out.println("Produto cadastrado");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Pessoa> listar() throws RemoteException{
        ArrayList<Pessoa> listaPessoa = new ArrayList<>();
        String sql = "SELECT * FROM pessoa";

        try (Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String escola = rs.getString("escola");

        //Caso ja tenha cadastros com campos vazios eles sao mostrados na lista da GUI de uma forma mais agradavel
                if(nome == null || nome.trim().isEmpty()){
                    nome = "Nao encontrado";
                }
                if(escola == null || escola.trim().isEmpty()){
                    escola = "Nao encontrado";
                }

                listaPessoa.add(new Pessoa(id, nome, escola));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPessoa;
    }

    public Pessoa buscarPorID(int idU) throws RemoteException{
        String sql = "SELECT * FROM pessoa WHERE id = ?";

        try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idU);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String escola = rs.getString("escola");

                return new Pessoa(id, nome, escola); //id encontrado
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; //id nn encontrado
    }

    public void atualizar(int id, String nome, String escola) throws RemoteException{
        String sql = "UPDATE pessoa SET nome = ?, escola = ? WHERE id = ?";

        try (Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, escola);
            stmt.setInt(3, id);
            // se houver alguma alteração "verifica" sera >0, senao sera =0
            int executeUpdate = stmt.executeUpdate();

            if (executeUpdate > 0) {
                System.out.println("Alteração efetuada");
            } else {
                System.out.println("ID nao encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletar(int id) throws RemoteException{
        String sql = "DELETE FROM pessoa WHERE id = ?";

        try (Connection conn = Conexao.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int executeUpdate = stmt.executeUpdate();

            if (executeUpdate > 0) {
                System.out.println("Alteração efetuada");
            } else {
                System.out.println("ID nao encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
