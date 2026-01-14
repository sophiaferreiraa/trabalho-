package BD_MCV_RMI_PROXY.Servidor.Model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface PessoaInterface extends Remote{
    public void cadastrar(int id, String nome, String escola) throws RemoteException;
    public ArrayList<Pessoa> listar() throws RemoteException;
    public Pessoa buscarPorID(int idU) throws RemoteException;
    public void atualizar(int id, String nome, String escola) throws RemoteException;
    public void deletar(int id) throws RemoteException;
}
