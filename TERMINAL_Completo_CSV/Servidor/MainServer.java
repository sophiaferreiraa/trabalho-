package TERMINAL_Completo_CSV.Servidor;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import BD_MCV_RMI_PROXY.Servidor.Model.PessoaDAO;
import BD_MCV_RMI_PROXY.Servidor.Model.PessoaInterface;

public class MainServer {
    public static void main(String[] args) {
        try {
            PessoaInterface pessoa = new PessoaDAO();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi:///CompletoCSV_Terminal", pessoa);
            System.out.println("Servidor funcionando!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
