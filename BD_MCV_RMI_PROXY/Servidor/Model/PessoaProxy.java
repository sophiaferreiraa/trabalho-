package BD_MCV_RMI_PROXY.Servidor.Model;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PessoaProxy extends UnicastRemoteObject implements PessoaInterface{
    private PessoaInterface dao;

    public PessoaProxy() throws RemoteException{
        try {
            dao = new PessoaDAO();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cadastrar(int id, String nome, String escola) throws RemoteException {
    // id nao pode ja estar cadastrado na lista
        ArrayList<Pessoa> lista = dao.listar();
        boolean verifica = false;
        for (Pessoa pessoa : lista) {
            if(pessoa.id == id){
                verifica = true;//se encontrar um id igual sair do for com verifica igua a verdadeira
                break;
            }
        }

        if(!verifica){
            dao.cadastrar(id, nome, escola);
        }else{
            throw new RemoteException("ID ja cadastrado");
        }
    }

    @Override
    public ArrayList<Pessoa> listar() throws RemoteException {
        return dao.listar();
    }

    @Override
    public Pessoa buscarPorID(int idU) throws RemoteException {
        return dao.buscarPorID(idU);
    }

    @Override
    public void atualizar(int id, String nome, String escola) throws RemoteException {
        //id tem que estar cadastrado na lista
        ArrayList<Pessoa> lista = dao.listar();
        boolean verifica = false;
        for (Pessoa pessoa : lista) {
            if(pessoa.id == id){
                verifica = true;
                break;
            }
        }

        if(verifica){
            dao.atualizar(id, nome, escola);
        }else{
            throw new RemoteException("ID nao esta presente no banco");
        }
    }

    @Override
    public void deletar(int id) throws RemoteException {
         //id tem que estar cadastrado na lista
        ArrayList<Pessoa> lista = dao.listar();
        boolean verifica = false;
        for (Pessoa pessoa : lista) {
            if(pessoa.id == id){
                verifica = true;
                break;
            }
        }

        if(verifica){
            dao.deletar(id);
        }else{
            throw new RemoteException("ID nao esta presente no banco");
        }
    }
}
