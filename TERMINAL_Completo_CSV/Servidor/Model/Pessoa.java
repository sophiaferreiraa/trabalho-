package TERMINAL_Completo_CSV.Servidor.Model;

import java.io.Serializable;

public class Pessoa implements Serializable{
    int num;
    long matricula;
    String nome;
    
    public Pessoa(int num, long matricula, String nome) {
        this.num = num;
        this.matricula = matricula;
        this.nome = nome;
    }

    @Override
    public String toString() {
        return num + "," + matricula + "," + nome;
    }
}
