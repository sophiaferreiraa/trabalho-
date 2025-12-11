package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;

import model.Aluno;
import view.AlunoComponent;
import view.JanelaPrincipal;

public class AlunoController {
	
	private JanelaPrincipal view;
	private Aluno model;

	public AlunoController(Aluno model, JanelaPrincipal view) {
		this.view = view;
		this.model = model;
		
		setListeners();
	}
	
	public void setListeners() {
		
		view.addListenerAddButton(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (validate()) {
						salvarAluno();
						loadAlunos();
						view.clear();
						view.showMessage("Aluno salvo");
					} else {
						view.showMessage("Matricula já existente");
					}
				} catch (NumberFormatException e) {
					view.showMessage("Matricula inválida");
				}
			}
		});

		ArrayList<AlunoComponent> lista = view.getComponentList();
		
		for (int aux = 0; aux < lista.size(); aux++) {
			
			final int i = aux;
			lista.get(i).addListenerUpdateButton(new ActionListener() {
				private int id;

				@Override
				public void actionPerformed(ActionEvent e) {	
					if (lista.get(i).getButtonEditar().getText().equals("Editar")) {
						this.id = lista.get(i).getMatricula();
						lista.get(i).getButtonEditar().setText("Salvar");
						lista.get(i).setBackground(Color.CYAN);
						lista.get(i).setEnableComponents(true);
					} else if (lista.get(i).getButtonEditar().getText().equals("Salvar")) {
						lista.get(i).getButtonEditar().setText("Editar");
						lista.get(i).setBackground(lista.get(i).getParent().getBackground());
						updateAluno(id, lista.get(i).getMatricula(), lista.get(i).getName());
						lista.get(i).setEnableComponents(false);
					}
				}
			});
		}

		for (int aux = 0; aux < lista.size(); aux++) {

			final int i = aux;
			lista.get(i).addListenerDeleteButton(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					view.showMessage("Aluno " + lista.get(i).getName() + " removido!");
					deleteAluno(lista.get(i).getMatricula());
				}
			});
		}
	}

	public boolean validate() {

		int matricula = view.getMatricula();
		String nome = view.getNameAluno();

		ArrayList<Aluno> alunos = model.getAlunos();
		for (int i = 0; i < alunos.size(); i++) {
			if (alunos.get(i).getMatricula() == matricula) {
				return false;
			}
		}

		return true;
	}

	public void salvarAluno() {
		int matricula = view.getMatricula();
		String nome = view.getNameAluno();
		model.addAluno(matricula, nome);
	}

	public void updateAluno(int id, int matricula, String nome) {
		ArrayList<Aluno> alunos = model.getAlunos();
		for (int i = 0; i < alunos.size(); i++) {
			if (alunos.get(i).getMatricula() == id) {
				alunos.get(i).setMatricula(matricula);
				alunos.get(i).setNome(nome);
				loadAlunos();
				return;
			}
		}
	}

	public void deleteAluno(int matricula) {
		ArrayList<Aluno> alunos = model.getAlunos();
		for (int i = 0; i < alunos.size(); i++) {
			if (alunos.get(i).getMatricula() == matricula) {
				alunos.remove(i);
				loadAlunos();
				return;
			}
		}
	}
	
	private void loadAlunos() {
		view.getPanelAlunos().removeAll();
		view.getPanelAlunos().repaint();
		
		ArrayList<AlunoComponent> lista = view.getComponentList();
		
		JLabel labelMatricula = new JLabel("Matricula");
		labelMatricula.setPreferredSize(new Dimension(80, 20));
		view.getPanelAlunos().add(labelMatricula);
		
		JLabel labelNome = new JLabel("Nome");
		labelNome.setPreferredSize(new Dimension(380, 20));
		view.getPanelAlunos().add(labelNome);
		
		ArrayList<Aluno> alunos = model.getAlunos();
		lista.clear();
		
		for(int i=0;i<alunos.size();i++) {
			lista.add(new AlunoComponent(alunos.get(i).getMatricula()+"", alunos.get(i).getNome()));
		}
		
		for(int i=0;i<lista.size();i++) {
			view.getPanelAlunos().add(lista.get(i));
		}
		
		view.getPanelAlunos().setPreferredSize(new Dimension(200, alunos.size()*50));
		
		view.repaint();
		view.validate();
		
		setListeners();
	}
}