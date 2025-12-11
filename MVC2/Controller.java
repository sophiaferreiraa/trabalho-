import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Controller {

    private Model model;
    private View view;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        init();
    }

    private void init() {
        model.carregarEstudantes("estudantes.csv");
        
        view.atualizarTabela(model.getTodosEstudantes());

        view.addBuscarListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String termo = view.getTermoBusca();
                ArrayList<Estudante> resultado = model.buscarEstudantes(termo);
                view.atualizarTabela(resultado);
            }
        });
    }
}