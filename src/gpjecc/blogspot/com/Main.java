package gpjecc.blogspot.com;

import javax.swing.*;

import gpjecc.blogspot.com.network.GameServerMain;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main extends JFrame {

    public Main() {
        setTitle("Tartarugas Ninja - Multiplayer Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setResizable(false);

        // Layout simples
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Escolha o modo de jogo", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(title, BorderLayout.NORTH);

        // Botões
        JButton hostButton = new JButton("Host");
        JButton gameButton = new JButton("Game");

        hostButton.setFont(new Font("Arial", Font.PLAIN, 16));
        gameButton.setFont(new Font("Arial", Font.PLAIN, 16));

        // Painel para botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(hostButton);
        buttonPanel.add(gameButton);
        panel.add(buttonPanel, BorderLayout.CENTER);

        add(panel);

        // Ações dos botões
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });

        gameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startClient();
            }
        });
    }

    private void startServer() {
        try {
            GameServerMain.initServer();
            JOptionPane.showMessageDialog(this, "Servidor iniciado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o servidor:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startClient() {
        try {
            DesktopLauncher.initGame();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao iniciar o jogo:\n" + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
