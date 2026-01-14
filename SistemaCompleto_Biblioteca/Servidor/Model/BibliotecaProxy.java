package SistemaCompleto_Biblioteca.Servidor.Model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class BibliotecaProxy extends UnicastRemoteObject implements InterfaceDAO{
    private InterfaceDAO dao;

    public BibliotecaProxy() throws RemoteException{
        super();
        try {
            dao = new BibliotecaDAO();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(Biblioteca biblioteca) throws RemoteException {
        ArrayList<Biblioteca> lista = dao.listar();
        for (Biblioteca biblioteca2: lista) {
            if(biblioteca2.ISBN != biblioteca.ISBN && biblioteca.ano > 2000){
                dao.cadastrar(biblioteca);
            }
        }

    }

    @Override
    public ArrayList<Biblioteca> listar() throws RemoteException {
        return dao.listar();
    }

    

    
}
