package SistemaCompleto_Biblioteca.Servidor.Model;

import java.io.Serializable;

public class Biblioteca implements Serializable{
    String nomeLivro;
    int ISBN, ano;
    
    public Biblioteca(String nomeLivro, int iSBN, int ano) {
        this.nomeLivro = nomeLivro;
        ISBN = iSBN;
        this.ano = ano;
    }

    @Override
    public String toString() {
        return nomeLivro + "," + ISBN + "," + ano;
    }

    
}
