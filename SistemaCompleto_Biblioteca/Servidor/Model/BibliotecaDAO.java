package SistemaCompleto_Biblioteca.Servidor.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import MVC_Basico.Pessoa;

public class BibliotecaDAO extends UnicastRemoteObject implements InterfaceDAO{

    protected BibliotecaDAO() throws RemoteException {
        super();
    }

    String nomeArquivo = "C:\\Users\\Sophia\\Documents\\ProvaAlisson2\\MVC\\SistemaCompleto_Biblioteca\\biblioteca.csv";
    ArrayList<Biblioteca> lista = new ArrayList<>();

    public void lerCSV(){
        lista.clear(); // ← ESSENCIAL

        try (BufferedReader b = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            b.readLine(); // pular cabeçario
            while ((linha = b.readLine()) != null) {
                // Verifica se a linha não está vazia antes de processar
                if (!linha.trim().isEmpty()) {
                    String[] divideLinha = linha.split(",");
                    String nomeLivro = divideLinha[0];
                    int ISBN = Integer.parseInt(divideLinha[1]);
                    int ano = Integer.parseInt(divideLinha[2]);

                    lista.add(new Biblioteca(nomeLivro, ISBN, ano));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escreverCSV() {
        try (PrintWriter p = new PrintWriter(new FileWriter(nomeArquivo))) {
            p.println("Nome,ISBN,ano"); // adcionar cabeçario
            for (Biblioteca biblioteca : lista) {
                p.println(biblioteca);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cadastrar(Biblioteca biblioteca) throws RemoteException{
        lerCSV();
        lista.add(new Biblioteca(biblioteca.nomeLivro, biblioteca.ISBN, biblioteca.ano));
        escreverCSV();
    }

    public ArrayList<Biblioteca> listar() throws RemoteException{
        lerCSV();
        return lista;
    }
}
