package TERMINAL_Completo_CSV.Cliente.Controller;

import java.rmi.RemoteException;
import java.util.ArrayList;

import TERMINAL_Completo_CSV.Servidor.Model.PessoaInterface;
import TERMINAL_Completo_CSV.Servidor.Model.Pessoa;
import TERMINAL_Completo_CSV.Cliente.View.PessoaView;

public class PessoaController {
    private PessoaInterface model;
    private PessoaView view;

    public PessoaController(PessoaInterface model, PessoaView view) {
        this.model = model;
        this.view = view;

        executar();
    }

    public void executar() {
        int opcao = view.mostrarOpcoes();

        while (true) {

            switch (opcao) {
                case 1:
                    cadastrar();
                    break;

                case 2:
                    listar();
                    break;

                case 3:
                    atualizar();
                    break;

                case 4:
                    deletar();
                    break;

                default:
                    break;
            }
        }
    }

    public void cadastrar() {
        try {
            int num = view.getNum();
            long matricula = view.getMatricula();
            String nome = view.getNome();

            model.create(num, matricula, nome);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void listar() {
        try {
            ArrayList<Pessoa> lista = model.read();
            for (Pessoa pessoa : lista) {
                System.out.println(pessoa.toString() + "\n");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void atualizar() {
        try {
            int num = view.getNum();
            long matricula = view.getMatricula();
            String nome = view.getNome();

            model.update(num, matricula, nome);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void deletar() {
        try {
            int num = view.getNum();

            model.delete(num);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
