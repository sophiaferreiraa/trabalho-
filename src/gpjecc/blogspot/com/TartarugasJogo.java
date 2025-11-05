package gpjecc.blogspot.com;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.Screens.TelaLobby;
import gpjecc.blogspot.com.Screens.TelaMenu;
import gpjecc.blogspot.com.Screens.TelaNivel1;
import gpjecc.blogspot.com.Screens.TelaNivel2;
import gpjecc.blogspot.com.Screens.TelaVitoria;
import gpjecc.blogspot.com.network.GameServerImpl;
import gpjecc.blogspot.com.network.GameServerMain;
import gpjecc.blogspot.com.network.RmiGameClient;
import java.rmi.server.UnicastRemoteObject; // Import para desligar o servidor

public class TartarugasJogo extends Game {

    private SpriteBatch batch;
    private Screen currentScreen;

    // --- ESTADO GLOBAL DE REDE ---
    public RmiGameClient rmiClient;
    private GameServerImpl hostServer; 
    private boolean isServerRunning = false; // Flag para evitar erro de "Porta em Uso"
    // ----------------------------

    public TartarugasJogo() {
        // Construtor vazio
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        AssetsManager.getInstance();
        goToMenu();
    }

    private void changeScreen(Screen newScreen) {
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        currentScreen = newScreen;
        setScreen(currentScreen);
    }
    
    // --- MÉTODOS DE NAVEGAÇÃO E REDE ---

    public void goToMenu() {
        Gdx.app.log("TartarugasJogo", "Indo para o Menu Principal");
        changeScreen(new TelaMenu(this));
    }

    // NOVA LÓGICA (Criar Jogo - SOLO)
    public void startHostAndPlaySolo(String playerName) {
        Gdx.app.log("TartarugasJogo", "Iniciando Host (Solo)...");
        if (isServerRunning) return; // Segurança
        
        try {
            this.hostServer = GameServerMain.initServer();
            this.isServerRunning = true;
            
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect("127.0.0.1", playerName);
            
            // Define-se como host
            this.hostServer.setHostId(this.rmiClient.playerId);
            
            // Inicia o jogo imediatamente
            this.rmiClient.hostStartGame();
            
            // Vai direto para o jogo (pula lobby)
            startGame();

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao iniciar o host (Solo)!");
            this.isServerRunning = false; // Reseta a flag
            goToMenu(); 
        }
    }

    // LÓGICA ANTIGA (Criar Jogo - DUO)
    public void startHostAndGoToLobby(String playerName) {
        Gdx.app.log("TartarugasJogo", "Iniciando Host (Duo)...");
        if (isServerRunning) return; // Segurança

        try {
            this.hostServer = GameServerMain.initServer();
            this.isServerRunning = true;
            
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect("127.0.0.1", playerName);
            
            this.hostServer.setHostId(this.rmiClient.playerId);
            
            Gdx.app.log("TartarugasJogo", "Host conectado. Indo para o Lobby.");
            changeScreen(new TelaLobby(this, this.rmiClient, true)); // true = isHost

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao iniciar o host (Duo)!");
            this.isServerRunning = false; // Reseta a flag
            goToMenu(); 
        }
    }

    // LÓGICA ANTIGA (Entrar no Jogo)
    public void joinGameAndGoToLobby(String ip, String playerName) {
        Gdx.app.log("TartarugasJogo", "Tentando conectar em " + ip);
        try {
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect(ip, playerName);
            
            Gdx.app.log("TartarugasJogo", "Conectado! Indo para o Lobby.");
            changeScreen(new TelaLobby(this, this.rmiClient, false)); // false = não é host

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao conectar!");
            goToMenu();
        }
    }

    // Chamado pelo TelaLobby ou startHostAndPlaySolo
    public void startGame() {
        Gdx.app.log("TartarugasJogo", "Iniciando o Jogo (Nível 1)!");
        changeScreen(new TelaNivel1(this, this.rmiClient));
    }

    // Métodos antigos de navegação
    public void goToWinScreen(int nextLevel) {
        Gdx.app.log("TartarugasJogo", "Indo para Tela de Vitoria (prox: " + nextLevel + ")");
        changeScreen(new TelaVitoria(this, nextLevel));
    }

    public void goToLevel2() {
        Gdx.app.log("TartarugasJogo", "Indo para Nível 2");
        changeScreen(new TelaNivel2(this)); 
    }
    // ---------------------------------

    @Override
    public void render() {
        super.render(); 
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        if (rmiClient != null) {
            rmiClient.disconnect();
        }
        // Desliga o servidor se fomos nós que o iniciamos
        if (isServerRunning && hostServer != null) {
            try {
                UnicastRemoteObject.unexportObject(hostServer, true);
                Gdx.app.log("TartarugasJogo", "Servidor RMI desligado.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AssetsManager.getInstance().dispose();
    }
}