package TERMINAL_Completo_CSV.Servidor.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PessoaDAO extends UnicastRemoteObject implements PessoaInterface{

    public PessoaDAO() throws RemoteException {
        super();
    }

    ArrayList<Pessoa> listaAlunos = new ArrayList<>();
    String nomeArquivo = "C:\\Users\\Sophia\\Documents\\ProvaAlisson2\\MVC\\TERMINAL_Completo_CSV\\dados.csv";

    public void lerCSV() {
        listaAlunos.clear(); // ← ESSENCIAL

        try (BufferedReader b = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            b.readLine(); // pular cabeçario
            while ((linha = b.readLine()) != null) {
                // Verifica se a linha não está vazia antes de processar
                if (!linha.trim().isEmpty()) {
                    String[] divideLinha = linha.split(",");
                    int codigo = Integer.parseInt(divideLinha[0]);
                    long matricula = Long.parseLong(divideLinha[1]);
                    String nome = divideLinha[2];

                    listaAlunos.add(new Pessoa(codigo, matricula, nome));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void escreverCSV() {
        try (PrintWriter p = new PrintWriter(new FileWriter(nomeArquivo))) {
            p.println("Num,Matricula, Nome"); // adcionar cabeçario
            for (Pessoa pessoa : listaAlunos) {
                p.println(pessoa);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void create(int num, long matricula, String nome) throws RemoteException{ // cadastrar novo aluno no arquivo
        lerCSV();
        listaAlunos.add(new Pessoa(num, matricula, nome));
        escreverCSV();
    }

    public ArrayList<Pessoa> read() throws RemoteException{ // ler todos os alunos do arquivo
        lerCSV();
        return listaAlunos;
    }

    public void update(int num, long matricula, String nome) throws RemoteException{ // alterar a matricula e o nome de um aluno pelo numero
        lerCSV();
        for (Pessoa pessoa : listaAlunos) {
            if (pessoa.num == num) {
                pessoa.matricula = matricula;
                pessoa.nome = nome;
            }
        }
        escreverCSV();// nao colocar nem lerCSV nem escreverCSV dentro de loop
    }

    public void delete(int num) throws RemoteException{ // deletar um aluno do csv pelo numero
        lerCSV();
        // percorre a lista de trás pra frente
        for (int i = listaAlunos.size() - 1; i >= 0; i--) { // nao usar foreach no delete!!!!!
            if (listaAlunos.get(i).num == num) {
                listaAlunos.remove(i);
            }
        }
        escreverCSV();
    }
}
