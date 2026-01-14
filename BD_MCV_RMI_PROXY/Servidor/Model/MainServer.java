package BD_MCV_RMI_PROXY.Servidor.Model;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class MainServer {
    public static void main(String[] args) {
        try {
            PessoaInterface pessoa = new PessoaProxy();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi:///BD_Completo", pessoa);
            System.out.println("Servidor funcionando!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
