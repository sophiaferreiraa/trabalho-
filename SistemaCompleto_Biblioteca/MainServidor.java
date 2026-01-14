package SistemaCompleto_Biblioteca;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import SistemaCompleto_Biblioteca.Servidor.Model.BibliotecaProxy;
import SistemaCompleto_Biblioteca.Servidor.Model.InterfaceDAO;

public class MainServidor {
    public static void main(String[] args) {
        try {
            InterfaceDAO dao = new BibliotecaProxy();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi:///SistemaCompleto", dao);
            System.out.println("Servidor funcionando");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
