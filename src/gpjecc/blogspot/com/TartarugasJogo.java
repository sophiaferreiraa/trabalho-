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
import java.rmi.server.UnicastRemoteObject; 

public class TartarugasJogo extends Game {

    private SpriteBatch batch;
    private Screen currentScreen;

    public RmiGameClient rmiClient;
    private GameServerImpl hostServer; 
    private boolean isServerRunning = false; 
    private boolean isHost = false; // Adiciona flag para saber se somos o Host

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

    public void startHostAndPlaySolo(String playerName) {
        Gdx.app.log("TartarugasJogo", "Iniciando Host (Solo)...");
        if (isServerRunning) return; 
        
        try {
            this.hostServer = GameServerMain.initServer();
            this.isServerRunning = true;
            this.isHost = true; // Eu sou o host
            
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect("127.0.0.1", playerName);
            
            this.hostServer.setHostId(this.rmiClient.playerId);
            this.rmiClient.hostStartGame();
            
            startGame();

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao iniciar o host (Solo)!");
            this.isServerRunning = false; 
            this.isHost = false;
            goToMenu(); 
        }
    }

    public void startHostAndGoToLobby(String playerName) {
        Gdx.app.log("TartarugasJogo", "Iniciando Host (Duo)...");
        if (isServerRunning) return; 

        try {
            this.hostServer = GameServerMain.initServer();
            this.isServerRunning = true;
            this.isHost = true; // Eu sou o host
            
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect("127.0.0.1", playerName);
            
            this.hostServer.setHostId(this.rmiClient.playerId);
            
            Gdx.app.log("TartarugasJogo", "Host conectado. Indo para o Lobby.");
            changeScreen(new TelaLobby(this, this.rmiClient, true)); 

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao iniciar o host (Duo)!");
            this.isServerRunning = false; 
            this.isHost = false;
            goToMenu(); 
        }
    }

    public void joinGameAndGoToLobby(String ip, String playerName) {
        Gdx.app.log("TartarugasJogo", "Tentando conectar em " + ip);
        try {
            this.rmiClient = new RmiGameClient();
            this.rmiClient.connect(ip, playerName);
            this.isHost = false; // Eu sou um cliente
            
            Gdx.app.log("TartarugasJogo", "Conectado! Indo para o Lobby.");
            changeScreen(new TelaLobby(this, this.rmiClient, false)); 

        } catch (Exception e) {
            e.printStackTrace();
            Gdx.app.log("TartarugasJogo", "Falha ao conectar!");
            goToMenu();
        }
    }

    public void startGame() {
        Gdx.app.log("TartarugasJogo", "Iniciando o Jogo (Nível 1)!");
        changeScreen(new TelaNivel1(this, this.rmiClient));
    }

    public void goToWinScreen(int nextLevel) {
        Gdx.app.log("TartarugasJogo", "Indo para Tela de Vitoria (prox: " + nextLevel + ")");
        changeScreen(new TelaVitoria(this, nextLevel));
    }

    public void goToLevel2() {
        Gdx.app.log("TartarugasJogo", "Indo para Nível 2");
        
        // --- LÓGICA DE TRANSIÇÃO DE NÍVEL ---
        // Se eu sou o host, eu digo ao servidor para carregar o próximo nível
        if (isHost && rmiClient != null) {
            Gdx.app.log("TartarugasJogo", "Sou o Host, mandando servidor carregar Nível 2...");
            rmiClient.loadNextLevel();
        }
        
        // Todos os clientes (incluindo o host) mudam para a tela do Nível 2
        changeScreen(new TelaNivel2(this, this.rmiClient)); 
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