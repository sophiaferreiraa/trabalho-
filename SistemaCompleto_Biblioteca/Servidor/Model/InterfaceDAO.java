package SistemaCompleto_Biblioteca.Servidor.Model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface InterfaceDAO extends Remote{
    public void cadastrar(Biblioteca biblioteca) throws RemoteException;
    public ArrayList<Biblioteca> listar() throws RemoteException;
}
