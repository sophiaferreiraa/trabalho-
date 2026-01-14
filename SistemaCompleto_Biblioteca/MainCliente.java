package SistemaCompleto_Biblioteca;

import java.rmi.Naming;

import SistemaCompleto_Biblioteca.Cliente.BibliotecaController;
import SistemaCompleto_Biblioteca.Cliente.BibliotecaView;
import SistemaCompleto_Biblioteca.Servidor.Model.InterfaceDAO;

public class MainCliente {
    public static void main(String[] args) {
        try {
            InterfaceDAO dao = (InterfaceDAO) Naming.lookup("rmi:///SistemaCompleto");
            BibliotecaController controller = new BibliotecaController(dao);
            BibliotecaView view = new BibliotecaView(controller);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
