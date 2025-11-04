package gpjecc.blogspot.com;

import javax.swing.*;
import gpjecc.blogspot.com.network.GameServerMain;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        setTitle("Tartarugas Ninja - SERVER LAUNCHER"); // Título mudado
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Controle do Servidor", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // Botão ÚNICO
        JButton hostButton = new JButton("Iniciar Servidor (Host)");
        hostButton.setFont(new Font("Arial", Font.PLAIN, 16));

        // Painel para botão
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 30));
        buttonPanel.add(hostButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        // Ação do botão
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
                hostButton.setEnabled(false); // Desabilita após clicar
                hostButton.setText("Servidor Rodando");
            }
        });
    }

    private void startServer() {
        try {
            GameServerMain.initServer();
            JOptionPane.showMessageDialog(this, "Servidor iniciado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o servidor:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // O método startClient() foi REMOVIDO.

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}