package TERMINAL_Completo_CSV.Cliente;

import java.rmi.Naming;

import TERMINAL_Completo_CSV.Cliente.View.PessoaView;
import TERMINAL_Completo_CSV.Servidor.Model.PessoaInterface;
import TERMINAL_Completo_CSV.Cliente.Controller.PessoaController;

public class MainCliente {
    public static void main(String[] args) {
        try {
            PessoaInterface model = (PessoaInterface) Naming.lookup("rmi:///CompletoCSV_Terminal");
            PessoaView view = new PessoaView();
            PessoaController controller = new PessoaController(model, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
