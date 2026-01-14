package BD_MCV_RMI_PROXY.Servidor.Model;

import java.io.Serializable;

public class Pessoa implements Serializable{
    int id;
    String nome;
    String escola;
    
    public Pessoa(int id, String nome, String escola) {
        this.id = id;
        this.nome = nome;
        this.escola = escola;
    }

    @Override
    public String toString() {
        return "Pessoa [id=" + id + ", nome=" + nome + ", escola=" + escola + "]\n";
    }
}
