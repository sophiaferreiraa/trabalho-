package TERMINAL_Completo_CSV.Servidor.Model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PessoaInterface extends Remote{
    public void create(int num, long matricula, String nome) throws RemoteException;
    public ArrayList<Pessoa> read() throws RemoteException;
    public void update(int num, long matricula, String nome) throws RemoteException;
    public void delete(int num) throws RemoteException;
}
