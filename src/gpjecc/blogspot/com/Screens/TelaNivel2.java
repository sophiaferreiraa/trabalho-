package gpjecc.blogspot.com.Screens;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

import gpjecc.blogspot.com.TartarugasJogo;
import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.GameObjects.Enemy;
import gpjecc.blogspot.com.GameObjects.Player;
import gpjecc.blogspot.com.GameObjects.Blocks.Blocks;
import gpjecc.blogspot.com.GameObjects.Blocks.Ladders;

import gpjecc.blogspot.com.Observers.PlayerObserver;
import gpjecc.blogspot.com.network.RmiGameClient;
import gpjecc.blogspot.com.network.dto.EnemyState;
import gpjecc.blogspot.com.network.dto.PlayerInput;
import gpjecc.blogspot.com.network.dto.GameSnapshot;
import gpjecc.blogspot.com.network.dto.PlayerState;

public class TelaNivel2 implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player; 

    private Blocks blocks;
    private Rectangle victoryZone; 

    private OrthographicCamera camera;
    private float backgroundWidth;
    private float backgroundHeight;

    private BitmapFont font;
    private BitmapFont fontGameOver;

    private boolean gameOver = false;
    private boolean gameWon = false; 
    private float gameOverTimer = 0f;

    private final RmiGameClient rmiClient; 
    private final UUID localPlayerId; 
    private boolean connectionFailed = false; 
    
    private Map<UUID, Player> remotePlayers = new HashMap<>(); 
    private Map<UUID, Enemy> remoteEnemies = new HashMap<>(); 
    
    private SpriteBatch batch;

    public TelaNivel2(TartarugasJogo jogo, RmiGameClient rmiClient) {
        this.jogo = jogo;
        this.rmiClient = rmiClient; 
        
        if (this.rmiClient != null) {
            this.localPlayerId = rmiClient.playerId; 
            this.connectionFailed = false;
        } else {
            this.localPlayerId = null;
            this.connectionFailed = true; 
        }
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
        batch = jogo.getBatch();
    }

    @Override
    public void show() {
        // MUDANÇA: Carrega o fundo do Nível 2
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao_fase2.jpeg");
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        // MUDANÇA: Cliente desenha os blocos do Nível 2
        blocks = new Blocks(2); 

        player = new Player(); 
        player.addObserver(this); 

        // victoryZone = new Rectangle(2000, 50, 32, 172); // Lógica agora é do servidor

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);

        System.out.println("TelaNivel2 carregada!");
        if (connectionFailed) {
            System.out.println("Jogando em modo OFFLINE.");
        } else {
            System.out.println("Jogando ONLINE. Meu ID: " + localPlayerId);
        }
    }

    @Override
    public void render(float delta) {
        
        if (gameWon || gameOver) {
             gameOverTimer += delta;
             if (gameOverTimer >= 5f) {
                 // Se ganhou ou perdeu o Nível 2, volta ao menu
                 jogo.goToMenu();
             }
             return; 
        }

        // 1) Coletar input do jogador local
        PlayerInput input = new PlayerInput();
        if(player.isAlive()) {
            input.left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            input.right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            input.jump = Gdx.input.isKeyPressed(Input.Keys.SPACE);
            input.attack = Gdx.input.isKeyPressed(Input.Keys.Z);
            input.up = Gdx.input.isKeyPressed(Input.Keys.UP); 
            input.down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        }

        // 2) Enviar/Receber dados se estiver online
        if (!connectionFailed && rmiClient != null) {
            try {
                rmiClient.sendInput(input);
            } catch (Exception ex) {
                Gdx.app.log("TelaNivel2_Render", "Falha ao enviar input, conexão perdida.");
                connectionFailed = true; 
                jogo.goToMenu(); 
                return;
            }
            
            GameSnapshot snap;
            while ((snap = rmiClient.pollSnapshot()) != null) {
                try {
                    applySnapshotOnRenderThread(snap); 
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        // 4) Atualizar APENAS animações
        player.update(delta);
        for(Player remotePlayer : remotePlayers.values()) {
            remotePlayer.update(delta); 
        }
        for(Enemy remoteEnemy : remoteEnemies.values()) {
            remoteEnemy.update(delta);
        }

        // Câmera (centralizada no jogador local)
        if(player != null) {
            camera.position.x = player.getPosition().x;
            float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
            camera.position.x = Math.max(effectiveViewportWidth / 2, camera.position.x);
            float maxCameraX = Math.max(effectiveViewportWidth / 2, backgroundWidth - effectiveViewportWidth / 2);
            camera.position.x = Math.min(maxCameraX, camera.position.x);
            camera.update();
        }

        // Render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);
        
        if (blocks != null) {
            blocks.draw(batch);
        }

        player.draw(batch);
        for (Player p : remotePlayers.values()) {
            p.draw(batch);
        }
        for (Enemy e : remoteEnemies.values()) {
            e.draw(batch);
        }

        if (gameOver) {
            String text = "VOCÊ PERDEU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        }
        
        font.draw(batch, "Vida: " + player.getHealth(), camera.position.x - 500, camera.viewportHeight - 10);
        font.draw(batch, "Pontuação: " + player.getScore(), camera.position.x - 400, camera.viewportHeight - 10);

        batch.end();
    }

    @Override public void resize(int width, int height) { camera.setToOrtho(false, 1024, 256); camera.update(); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { 
        font.dispose(); 
        fontGameOver.dispose(); 
    }

    // --- Observer (Notifica sobre mudanças de HP/Score vindas do servidor) ---
    @Override public void onPlayerDamaged(float newHealth) { Gdx.app.log("OBSERVADOR", "Dano. Vida: " + newHealth); }
    @Override public void onPlayerAttack() { /* Não mais usado */ }
    @Override public void onScoreChanged(int newScore) { 
         Gdx.app.log("OBSERVADOR", "Pontos: " + newScore);
    }
    @Override public void onPlayerDied() { 
        Gdx.app.log("OBSERVADOR", "MORREU!"); 
        gameOver = true; 
        gameOverTimer = 0f; 
    }

    private void applySnapshotOnRenderThread(GameSnapshot snap) {
        if (snap == null) return;
        
        // --- CONDIÇÃO DE VITÓRIA (Nível 2) ---
        if ("VICTORY".equals(snap.gameState) && !gameWon) {
            gameWon = true; 
            Gdx.app.log("TelaNivel2", "Servidor reportou VITÓRIA! Indo para tela de vitória...");
            // MUDANÇA: Não há nível 3, então apenas vamos para a tela de vitória
            jogo.goToWinScreen(3); // (TelaVitoria irá para o Nível 3, que não existe)
            // Ou melhor, voltamos ao menu:
            // jogo.goToMenu();
            return; 
        }
        // ---------------------------------

        if (snap.players != null) {
            java.util.Set<java.util.UUID> serverPlayerIds = snap.players.keySet();
            for (PlayerState ps : snap.players.values()) {
                Player p = null; 
                if (ps.playerId.equals(localPlayerId)) {
                    p = this.player;
                } else {
                    p = remotePlayers.get(ps.playerId); 
                    if (p == null) {
                        Gdx.app.log("Rede", "Novo jogador fantasma criado: " + ps.name);
                        p = Player.createNetworkGhost(ps.x, ps.y);
                        p.setName(ps.name);
                        remotePlayers.put(ps.playerId, p);
                    }
                }
                
                p.setPosition(ps.x, ps.y);
                p.setHp(ps.hp);
                p.setScore(ps.score);
                p.setFacing(ps.facingRight);
                p.setVelocity(ps.vx, ps.vy); 
                p.setOnGround(ps.onGround);
                p.setClimbing(ps.isClimbing);
                p.setAttacking(ps.isAttacking);
                p.setDead(ps.isDead);
            }
            
            java.util.List<java.util.UUID> toRemove = new java.util.ArrayList<>(remotePlayers.keySet());
            toRemove.removeAll(serverPlayerIds);
            for (java.util.UUID rid : toRemove) {
                Gdx.app.log("Rede", "Removendo jogador fantasma: " + rid);
                remotePlayers.remove(rid);
            }
        }
        
        if (snap.enemies != null) {
            java.util.Set<java.util.UUID> serverEnemyIds = snap.enemies.keySet();
            for (EnemyState es : snap.enemies.values()) {
                Enemy remoteEnemy = remoteEnemies.get(es.id);
                if (remoteEnemy == null) {
                    Gdx.app.log("Rede", "Novo inimigo criado: " + es.id);
                    remoteEnemy = new Enemy(es.x, es.y); 
                    remoteEnemies.put(es.id, remoteEnemy);
                }
                remoteEnemy.setPosition(es.x, es.y);
                remoteEnemy.setFacing(es.facingRight);
            }
            
            java.util.List<java.util.UUID> toRemove = new java.util.ArrayList<>(remoteEnemies.keySet());
            toRemove.removeAll(serverEnemyIds); 
            for (java.util.UUID rid : toRemove) {
                Gdx.app.log("Rede", "Removendo inimigo: " + rid);
                remoteEnemies.remove(rid);
            }
        }
    }
}