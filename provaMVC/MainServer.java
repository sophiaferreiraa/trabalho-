package provaMVC;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import provaMVC.Model.FilmeInterface;
import provaMVC.Model.FilmeDAO;


public class MainServer {
    public static void main(String[] args) {
        try {
            FilmeInterface filme = new FilmeDAO();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi:///Filmes", filme);
            System.out.println("Servidor rodando");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
