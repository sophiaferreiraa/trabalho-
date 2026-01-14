package SistemaCompleto_Biblioteca.Cliente;

import java.rmi.RemoteException;
import java.util.ArrayList;

import SistemaCompleto_Biblioteca.Servidor.Model.Biblioteca;
import SistemaCompleto_Biblioteca.Servidor.Model.InterfaceDAO;

public class BibliotecaController {
    private InterfaceDAO dao;

    public BibliotecaController(InterfaceDAO dao) {
        this.dao = dao;
    }

    public void cadastrar(Biblioteca biblioteca){
        try {
            dao.cadastrar(biblioteca);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Biblioteca> listar(){
        try {
            return dao.listar();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
