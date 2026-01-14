package BD_MCV_RMI_PROXY.Cliente.View;

import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import BD_MCV_RMI_PROXY.Servidor.Model.PessoaDAO;

public class PessoaView {

    JTextField campoID, campoNome, campoEscola;
    JTextArea campoListar;
    JButton cadastrar, listar, buscarPorID, atualizar, deletar, limpar;

    public PessoaView(){
        janela();
    }

    public void janela() {
        JFrame janela = new JFrame("Sistema bd completo");
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setBounds(200, 400, 400, 600);

        JPanel painel = new JPanel();

        JTextField campoID = new JTextField(20);
        JTextField campoNome = new JTextField(20);
        JTextField campoEscola = new JTextField(20);
        JTextArea campoListar = new JTextArea(5, 20);
        JScrollPane rolagem = new JScrollPane(campoListar);

        JButton cadastrar = new JButton("Cadastrar");
        JButton listar = new JButton("Listar");
        JButton buscarPorID = new JButton("Buscar por ID");
        JButton atualizar = new JButton("Atualizar");
        JButton deletar = new JButton("Deletar");
        JButton limpar = new JButton("Limpar");

        limpar.addActionListener(e -> {
            campoID.setText("");
            campoNome.setText("");
            campoEscola.setText("");
        });

        painel.add(new JLabel("ID: "));
        painel.add(campoID);
        painel.add(new JLabel("Nome: "));
        painel.add(campoNome);
        painel.add(new JLabel("Escola: "));
        painel.add(campoEscola);
        painel.add(cadastrar);
        painel.add(listar);
        painel.add(buscarPorID);
        painel.add(atualizar);
        painel.add(deletar);
        painel.add(limpar);
        painel.add(rolagem);

        janela.add(painel);
        janela.setVisible(true);
    }

    public Integer getId(){
        return Integer.parseInt(campoID.getText());
    }

    public String getNome(){
        return campoNome.getText();
    }

    public String getEscola(){
        return campoEscola.getText();
    }

    public void setCampoListar(String texto){
        campoListar.setText(texto);
    }

    public void addCadastrar(ActionListener listener){
        cadastrar.addActionListener(listener);
    }

    public void addListar(ActionListener listener){
        listar.addActionListener(listener);
    }

    public void addBuscarPorID(ActionListener listener){
        buscarPorID.addActionListener(listener);
    }

    public void addAtualizar(ActionListener listener){
        atualizar.addActionListener(listener);
    }

    public void addDeletar(ActionListener listener){
        deletar.addActionListener(listener);
    }
}
