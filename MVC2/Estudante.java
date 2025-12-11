public class Estudante {
    
    private String matricula;
    private String nome;
    private String codigoCurso;

    public Estudante(String matricula, String nome, String codigoCurso) {
        this.matricula = matricula;
        this.nome = nome;
        this.codigoCurso = codigoCurso;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoCurso() {
        return codigoCurso;
    }

    public void setCodigoCurso(String codigoCurso) {
        this.codigoCurso = codigoCurso;
    }
}