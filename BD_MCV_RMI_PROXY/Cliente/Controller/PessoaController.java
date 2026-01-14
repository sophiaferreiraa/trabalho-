package BD_MCV_RMI_PROXY.Cliente.Controller;

import java.rmi.RemoteException;
import java.util.ArrayList;

import BD_MCV_RMI_PROXY.Cliente.View.PessoaView;
import BD_MCV_RMI_PROXY.Servidor.Model.Pessoa;
import BD_MCV_RMI_PROXY.Servidor.Model.PessoaInterface;

public class PessoaController {
    private PessoaInterface model;
    private PessoaView view;

    public PessoaController(PessoaInterface model, PessoaView view) {
        this.model = model;
        this.view = view;

        this.view.addCadastrar(e -> cadastrar());
        this.view.addCadastrar(e -> listar());
        this.view.addCadastrar(e -> buscarPorID());
        this.view.addCadastrar(e -> atualizar());
        this.view.addCadastrar(e -> deletar());
    }

    public void cadastrar(){
        try {
            int id = view.getId();
            String nome = view.getNome();
            String escola = view.getEscola();
            model.cadastrar(id, nome, escola);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void listar(){
        try {
            ArrayList<Pessoa>lista = model.listar();

            for (Pessoa pessoa : lista) {
                view.setCampoListar(pessoa.toString() + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void buscarPorID(){
        try {
            int id = view.getId();

            Pessoa pessoa = model.buscarPorID(id);

            view.setCampoListar(pessoa.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void atualizar(){
        try {
            int id = view.getId();
            String nome = view.getNome();
            String escola = view.getEscola();
            model.atualizar(id, nome, escola);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deletar(){
        try {
            int id = view.getId();
            model.deletar(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
