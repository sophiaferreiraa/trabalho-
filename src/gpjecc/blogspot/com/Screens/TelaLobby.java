package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import gpjecc.blogspot.com.TartarugasJogo;
import gpjecc.blogspot.com.network.RmiGameClient;
import gpjecc.blogspot.com.network.dto.LobbyState;
import java.util.UUID;

public class TelaLobby implements Screen {

    private final TartarugasJogo jogo;
    private final RmiGameClient rmiClient;
    private final boolean isHost;
    
    private SpriteBatch batch;
    private BitmapFont font;
    
    private LobbyState currentLobbyState;
    private float lobbyPollTimer = 0f;
    
    // Não precisamos mais de um botão "Iniciar Jogo"
    // private Rectangle btnIniciarBounds; // REMOVIDO

    public TelaLobby(TartarugasJogo jogo, RmiGameClient rmiClient, boolean isHost) {
        this.jogo = jogo;
        this.rmiClient = rmiClient;
        this.isHost = isHost;
        
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        font.setScale(1.2f); 
    }

    @Override
    public void show() {
        // Nada aqui
    }
    
    private void checarCliques() {
        // Não precisamos mais de cliques aqui
    }

    @Override
    public void render(float delta) {
        checarCliques(); // (Não faz nada, mas mantemos a estrutura)
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Atualiza o lobby a cada 1 segundo
        lobbyPollTimer -= delta;
        if (lobbyPollTimer <= 0) {
            lobbyPollTimer = 1.0f;
            fetchLobbyState();
        }
        
        batch.begin();
        
        font.draw(batch, "SALA DE ESPERA (LOBBY)", 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, isHost ? "VOCÊ É O HOST" : "VOCÊ É UM CLIENTE", 20, Gdx.graphics.getHeight() - 40);
        
        if (currentLobbyState != null) {
            int yPos = Gdx.graphics.getHeight() - 80;
            font.draw(batch, "Jogadores Conectados:", 20, yPos);
            yPos -= 20;
            
            for (UUID id : currentLobbyState.playerNames.keySet()) {
                String nome = currentLobbyState.playerNames.get(id);
                String sufixo = "";
                if (currentLobbyState.hostId != null && currentLobbyState.hostId.equals(id)) {
                    sufixo = " (Host)";
                }
                font.draw(batch, "- " + nome + sufixo, 40, yPos);
                yPos -= 20;
            }
            
            // --- NOVA LÓGICA DE AUTO-START ---
            // Se eu sou o Host e já tem 2 jogadores, eu inicio o jogo
            if (isHost && currentLobbyState.playerNames.size() >= 2) {
                Gdx.app.log("TelaLobby", "Dois jogadores detectados! Iniciando jogo...");
                rmiClient.hostStartGame(); // Diz ao servidor para mudar o estado
            }
            
        } else {
             font.draw(batch, "Conectando...", 20, Gdx.graphics.getHeight() - 80);
        }
        
        batch.end();
    }
    
    private void fetchLobbyState() {
        LobbyState newState = rmiClient.getLobbyState();
        if (newState == null) {
            Gdx.app.log("TelaLobby", "Falha ao buscar estado do lobby. Voltando ao menu.");
            jogo.goToMenu(); // Volta ao menu se a conexão cair
            return;
        }
        
        this.currentLobbyState = newState;
        
        // Se o estado mudou para "IN_GAME" (seja por Solo ou Duo), inicia o jogo
        if ("IN_GAME".equals(newState.gameState)) {
            Gdx.app.log("TelaLobby", "Servidor mudou para IN_GAME! Mudando de tela...");
            jogo.startGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        // Nada aqui
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}