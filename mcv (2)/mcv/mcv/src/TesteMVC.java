import controller.AlunoController;
import model.Aluno;
import view.JanelaPrincipal;

public class TesteMVC {
	
	/**
	 * @author Raphael
	 */
	
	public static void main(String[] args) {
		// modelo
		Aluno model = new Aluno();
		// visão
		JanelaPrincipal view = new JanelaPrincipal();
		// controlador
		AlunoController controller = new AlunoController(model, view);
	}
}
