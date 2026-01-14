package BD_MCV_RMI_PROXY.Cliente;

import java.rmi.Naming;

import BD_MCV_RMI_PROXY.Cliente.View.PessoaView;
import BD_MCV_RMI_PROXY.Servidor.Model.PessoaInterface;
import BD_MCV_RMI_PROXY.Cliente.Controller.PessoaController;

public class MainCliente {
    public static void main(String[] args) {
        try {
            PessoaInterface model = (PessoaInterface) Naming.lookup("rmi:///BD_Completo");
            PessoaView view = new PessoaView();
            PessoaController controller = new PessoaController(model, view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
