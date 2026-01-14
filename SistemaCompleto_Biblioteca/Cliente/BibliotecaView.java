package SistemaCompleto_Biblioteca.Cliente;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import SistemaCompleto_Biblioteca.Servidor.Model.Biblioteca;

public class BibliotecaView {
    private BibliotecaController controller;

    public BibliotecaView(BibliotecaController controller){
        this.controller = controller;
        janela();
    }

    public void janela() {
        JFrame janela = new JFrame("Sistema completo");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setBounds(200, 400, 400, 600);

        JPanel painel = new JPanel();

        JButton cadastrar = new JButton("Cadastrar/Create");
        JButton listar = new JButton("Listar/Read");
        JTextField nome = new JTextField(20);
        JTextField ISBN = new JTextField(20);
        JTextField ano = new JTextField(20);
        JTextArea listarBiblioteca = new JTextArea(20, 20);
        JScrollPane rolagem = new JScrollPane(listarBiblioteca);

        cadastrar.addActionListener(e -> {
            // JTextField recebe uma String por isso tem que adpitar para o tipo real da variavel
            String nome_ = nome.getText();
            int ISBN_ = Integer.parseInt(ISBN.getText());
            long ano_ = Long.parseLong(ano.getText());
            controller.cadastrar(new Biblioteca(nome_, ISBN_, ISBN_));

            // limpar campos apos clicar no botao
            nome.setText("");
            ISBN.setText("");
            ano.setText("");
        });

        listar.addActionListener(e -> {
            listarBiblioteca.append(controller.listar().toString());
        });

        painel.add(new JLabel("Nome: "));
        painel.add(nome);
        painel.add(new JLabel("ISBN: "));
        painel.add(ISBN);
        painel.add(new JLabel("Ano: "));
        painel.add(ano);
        painel.add(cadastrar);
        painel.add(listar);
        painel.add(rolagem); // nn precisa adcionar o JTextArea listarAlunos

        janela.add(painel);
        janela.setVisible(true);
    }
}
