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
import gpjecc.blogspot.com.network.dto.PlayerInput;
import gpjecc.blogspot.com.network.dto.GameSnapshot;
import gpjecc.blogspot.com.network.dto.PlayerState;

public class TelaNivel1 implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player; // Jogador Local

    private Blocks blocks;
    private Ladders ladders;
    private Rectangle victoryZone;

    private OrthographicCamera camera;
    private float backgroundWidth;
    private float backgroundHeight;

    private BitmapFont font;
    private BitmapFont fontGameOver;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private float gameOverTimer = 0f;

    // --- MODIFICAÇÕES DE REDE ---
    private final RmiGameClient rmiClient; // Recebido, não criado
    private Map<UUID, Player> remotePlayers = new HashMap<>(); 
    private final UUID localPlayerId; // Recebido
    private boolean connectionFailed = false; 
    // ----------------------------

    private SpriteBatch batch;
    private static final ConcurrentLinkedQueue<Runnable> audioActions = new ConcurrentLinkedQueue<>();

    // MODIFICAÇÃO: Construtor agora recebe o RmiGameClient
    public TelaNivel1(TartarugasJogo jogo, RmiGameClient rmiClient) {
        this.jogo = jogo;
        this.rmiClient = rmiClient; // Salva o cliente
        
        if (this.rmiClient != null) {
            this.localPlayerId = rmiClient.playerId; // Salva nosso ID
            this.connectionFailed = false;
        } else {
            // Modo offline (se o rmiClient for nulo)
            this.localPlayerId = null;
            this.connectionFailed = true; // Joga offline
        }
        
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
        batch = jogo.getBatch();
    }

    public static void enqueueAudio(Runnable action) {
        if (action != null) audioActions.offer(action);
    }

    @Override
    public void show() {
        // MODIFICAÇÃO: Lógica de conexão REMOVIDA daqui
        
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao.jpeg");
        if (backgroundTexture == null) {
            throw new RuntimeException("Erro: textura bueirao.jpeg não encontrada!");
        }
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        blocks = new Blocks(1);
        ladders = new Ladders(1);

        player = new Player(); // Cria o jogador local
        player.setLevelDependencies(blocks, ladders);
        player.addObserver(this); 

        Enemy.createEnemies(1);
        victoryZone = new Rectangle(2000, 50, 32, 172);

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);

        System.out.println("TelaNivel1 carregada!");
        if (connectionFailed) {
            System.out.println("Jogando em modo OFFLINE.");
        } else {
            System.out.println("Jogando ONLINE. Meu ID: " + localPlayerId);
        }
    }

    @Override
    public void render(float delta) {

        // 1) Coletar input do jogador local
        PlayerInput input = new PlayerInput();
        input.clientTimestamp = System.currentTimeMillis();
        input.left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        input.right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        input.jump = Gdx.input.isKeyPressed(Input.Keys.SPACE);
        input.attack = Gdx.input.isKeyPressed(Input.Keys.Z); 

        // Só envia/recebe dados se estiver online
        if (!connectionFailed && rmiClient != null) {
            // 2) Enviar input
            try {
                rmiClient.sendInput(input);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            // 3) Receber e aplicar snapshots
            GameSnapshot snap;
            while ((snap = rmiClient.pollSnapshot()) != null) {
                try {
                    applySnapshotOnRenderThread(snap);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        // 4) Lógica de update do jogador local
        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                if (gameOver) Gdx.app.exit();
            }
        }
        
        // 5) Update dos "fantasmas" (para animar ataques)
        for(Player remotePlayer : remotePlayers.values()) {
            remotePlayer.update(delta);
        }

        Runnable audioTask;
        while ((audioTask = audioActions.poll()) != null) {
            try { audioTask.run(); } catch (Throwable t) { t.printStackTrace(); }
        }

        AssetsManager.getInstance().update(delta);

        // Lógica de colisão com inimigos (LOCAL)
        if (!gameOver && !gameWon && player.isAlive()) {
            for (int i = 0; i < Enemy.enemies.size; i++) {
                Enemy enemy = Enemy.enemies.get(i);
                enemy.update(delta); 

                if (player.isAttacking() && player.collide(enemy)) {
                    enemy.takeDamage(1);
                    if (!enemy.isAlive()) {
                        Enemy.enemies.removeIndex(i);
                        i--;
                        player.addScore(100);
                    }
                } else if (player.collide(enemy)) {
                    // O log do usuário mostrou que isso funciona
                    player.tryTakeDamage(0.5f); 
                }
            }

            if (player.getBounds().overlaps(victoryZone)) {
                Gdx.app.log("TelaNivel1", "Jogador alcançou a saida!");
                gameWon = true; 
                jogo.goToWinScreen(2); 
                return; 
            }
        }

        // Câmera
        camera.position.x = player.getPosition().x;
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        camera.position.x = Math.max(effectiveViewportWidth / 2, camera.position.x);
        float maxCameraX = Math.max(effectiveViewportWidth / 2, backgroundWidth - effectiveViewportWidth / 2);
        camera.position.x = Math.min(maxCameraX, camera.position.x);
        camera.update();

        // Render
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);
        blocks.draw(batch);

        // 1. Desenha o jogador local
        player.draw(batch);
        
        // 2. Desenha os jogadores remotos
        for (Player p : remotePlayers.values()) {
            p.draw(batch);
        }

        Enemy.drawAll(batch);

        // HUD / Mensagens
        if (gameOver) {
            String text = "VOCÊ PERDEU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        }
        
        // HUD (HP 5.0)
        font.draw(batch, "Vida: " + player.getHealth(), camera.position.x - 500, camera.viewportHeight - 10);
        font.draw(batch, "Pontuação: " + player.getScore(), camera.position.x - 400, camera.viewportHeight - 10);

        batch.end();
    }

    @Override public void resize(int width, int height) { camera.setToOrtho(false, 1024, 256); camera.update(); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    @Override public void dispose() { Enemy.disposeAll(); font.dispose(); fontGameOver.dispose(); }

    // --- Observer (para o jogador LOCAL) ---
    @Override public void onPlayerDamaged(float newHealth) { Gdx.app.log("OBSERVADOR", "Dano. Vida: " + newHealth); }
    @Override public void onPlayerAttack() { Gdx.app.log("OBSERVADOR", "Ataque!"); }
    @Override public void onScoreChanged(int newScore) { 
         Gdx.app.log("OBSERVADOR", "Pontos: " + newScore);
        if (newScore >= 700 && !gameWon && player.isAlive()) {
            gameWon = true;
            gameOverTimer = 0f;
            jogo.goToWinScreen(2);
        }
    }
    @Override public void onPlayerDied() { Gdx.app.log("OBSERVADOR", "MORREU!"); gameOver = true; gameOverTimer = 0f; }


    // --- MÉTODO DE REDE CRÍTICO ---
    private void applySnapshotOnRenderThread(GameSnapshot snap) {
        if (snap == null || snap.players == null) return;

        java.util.Set<java.util.UUID> serverIds = snap.players.keySet();

        for (java.util.Map.Entry<java.util.UUID, PlayerState> e : snap.players.entrySet()) {
            java.util.UUID id = e.getKey();
            PlayerState ps = e.getValue();

            // É O JOGADOR LOCAL?
            if (id.equals(localPlayerId)) {
                player.setHp(ps.hp); 
                // (Opcional: Reconciliação de Posição)
                // player.setPosition(ps.x, ps.y); 
                continue; 
            }

            // É UM JOGADOR REMOTO ("FANTASMA")
            Player remotePlayer = remotePlayers.get(id);
            if (remotePlayer == null) {
                Gdx.app.log("Rede", "Novo jogador fantasma criado: " + ps.name);
                remotePlayer = Player.createNetworkGhost(ps.x, ps.y, this.blocks, this.ladders);
                remotePlayer.setName(ps.name);
                remotePlayers.put(id, remotePlayer);
            }

            // Atualiza o "fantasma"
            remotePlayer.setPosition(ps.x, ps.y);
            remotePlayer.setHp(ps.hp);
            remotePlayer.setFacing(ps.facingRight);
            
            if (ps.isAttacking) {
                remotePlayer.performNetworkAttack(); 
            }
        }

        // Remove players remotos que sumiram
        java.util.List<java.util.UUID> toRemove = new java.util.ArrayList<>();
        for (java.util.UUID remoteId : remotePlayers.keySet()) {
            if (!serverIds.contains(remoteId)) {
                toRemove.add(remoteId);
            }
        }
        for (java.util.UUID rid : toRemove) {
            Gdx.app.log("Rede", "Removendo jogador fantasma: " + rid);
            remotePlayers.remove(rid);
        }
    }
}