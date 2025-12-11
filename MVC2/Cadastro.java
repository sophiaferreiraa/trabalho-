import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Cadastro extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;

	JLabel lblMatricula, lblNome, lblSerie, lblCurso;
	JTextField txtMatricula, txtNome, txtSerie, txtCurso;
	JButton btnProximo, btnAnterior;

	int indiceEstudante;
	Vector<String> estudantes;

	public Cadastro() {
		
		montaJanela();
		
		indiceEstudante = 0;
		estudantes = carregaDados();
		
		carregaDadosNaJanela();
		
		this.setSize(new Dimension(320, 180));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void carregaDadosNaJanela() {		
		String linha = estudantes.get(indiceEstudante);
		String [] tokens = linha.split(";",-1);	
		txtNome.setText(tokens[0]);
		txtMatricula.setText(tokens[1]);
		txtSerie.setText(tokens[2]);
		txtCurso.setText(tokens[3]);
		this.setTitle(indiceEstudante + 1 + " - " +  tokens[1]);
		testaLimites();
	}

	private Vector<String> carregaDados() {
		Vector<String> lista = new Vector<>();
		try {
			Scanner leitor = new Scanner(new File("estudantes.csv"));			
			leitor.nextLine(); //Remove cabeçalho.					
			
			while (leitor.hasNextLine()) {
				lista.add(leitor.nextLine());			
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		return lista;
	}

	private void montaJanela() {

		lblMatricula = new JLabel("Matricula");
		txtMatricula = new JTextField(20);

		lblNome = new JLabel("Nome");
		txtNome = new JTextField(22);

		lblSerie = new JLabel("Serie");
		txtSerie = new JTextField(3);

		lblCurso = new JLabel("Curso");
		txtCurso = new JTextField(15);

		btnProximo = new JButton("Next");
		btnProximo.setPreferredSize(new Dimension(130, 26));
		btnProximo.addActionListener(this);
		
		btnAnterior = new JButton("Previus");
		btnAnterior.setPreferredSize(new Dimension(130, 26));
		btnAnterior.addActionListener(this);
		// this.setLayout();

		Box painel = Box.createVerticalBox();

		Component box = Box.createRigidArea(new Dimension(30, 30));

		// Container painel = new JPanel(new FlowLayout());

		painel.add(novaLinha(box, lblMatricula, txtMatricula));
		painel.add(novaLinha(lblNome, txtNome));
		painel.add(novaLinha(lblSerie, txtSerie, lblCurso, txtCurso));

		painel.add(novaLinha(btnAnterior, box, btnProximo));

		this.add(painel);
	}

	private JPanel novaLinha(Component... componentes) {
		return novaLinha(FlowLayout.TRAILING, componentes);
	}

	private JPanel novaLinha(int alinhamento, Component... componentes) {
		// alinhamento= FlowLayout.TRAILING;
		JPanel painel = new JPanel(new FlowLayout(alinhamento, 3, 2));
		for (Component component : componentes) {
			painel.add(component);
		}
		return painel;
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(btnProximo.getActionCommand()) ) {
			indiceEstudante++;
		}else {
			indiceEstudante--;
		}
		carregaDadosNaJanela();	
	}


	private void testaLimites() {
				
		if(indiceEstudante == 0){
			btnAnterior.setEnabled(false);
			btnProximo.setEnabled(true);
			
		}else if(indiceEstudante  == estudantes.size()-1) {		
			btnAnterior.setEnabled(true);
			btnProximo.setEnabled(false);		
		}else {
			btnAnterior.setEnabled(true);
			btnProximo.setEnabled(true);
		}
		
	}

	public static void main(String[] args) {
		new Cadastro();

	}

}