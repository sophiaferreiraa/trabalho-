import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Model {

    private ArrayList<Estudante> listaEstudantes = new ArrayList<>();

    public void carregarEstudantes(String caminho) {
        String linha = "";
        String divisor = ";";

        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(divisor);
                if (dados.length == 3) {
                    Estudante est = new Estudante(dados[0], dados[1], dados[2]);
                    listaEstudantes.add(est);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Estudante> buscarEstudantes(String termo) {
        if (termo == null || termo.isEmpty()) {
            return listaEstudantes;
        }

        ArrayList<Estudante> resultado = new ArrayList<>();
        for (Estudante est : listaEstudantes) {
            if (est.getNome().toLowerCase().contains(termo.toLowerCase())) {
                resultado.add(est);
            }
        }
        return resultado;
    }
    
    public ArrayList<Estudante> getTodosEstudantes() {
        return listaEstudantes;
    }
}