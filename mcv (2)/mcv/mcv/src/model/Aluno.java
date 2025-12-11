package model;

import java.util.ArrayList;

/*
 * 
 * 	Modelo
 * 
 */

public class Aluno {
	
	/**
	 * @author Raphael
	 */
	
	private int matricula;
	private String nome;
	
	public Aluno() {}
	
	public Aluno(int matricula, String nome) {
		this.matricula = matricula;
		this.nome = nome;
	}
	
	public int getMatricula() {
		return matricula;
	}
	
	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	/*
	 * 	Simulação de uma base de dados
	 */
	private ArrayList<Aluno> alunos = new ArrayList<>();
	
	public ArrayList<Aluno> getAlunos(){
		
		return alunos;
		
	}
	
	public void addAluno(int matricula, String nome) {
		alunos.add(new Aluno(matricula, nome));
	}
}
