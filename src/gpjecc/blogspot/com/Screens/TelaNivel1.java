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

/**
 * TelaNivel1 — versão com fila de ações de áudio e processamento seguro de snapshots.
 * - Não cria RmiGameClient no render()
 * - Não executa áudio em threads de rede; use TelaNivel1.enqueueAudio(...)
 */
public class TelaNivel1 implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player;

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

    private RmiGameClient rmiClient;
    private Map<UUID, Player> players = new HashMap<>();

    private SpriteBatch batch;

    // Fila para ações que devem rodar na thread principal (p.ex.: tocar sons)
    private static final ConcurrentLinkedQueue<Runnable> audioActions = new ConcurrentLinkedQueue<>();

    public TelaNivel1(TartarugasJogo jogo) {
        this.jogo = jogo;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();

        // pega o batch já criado pelo jogo (deve ser único e criado no create())
        batch = jogo.getBatch();
    }

    /**
     * Permite que outros componentes (ex: listener RMI) enfileirem ações de áudio
     * para serem executadas no thread GL (render).
     */
    public static void enqueueAudio(Runnable action) {
        if (action != null) audioActions.offer(action);
    }

    @Override
    public void show() {
        try {
            rmiClient = new RmiGameClient();
            rmiClient.connect("127.0.0.1");
            System.out.println("Conectado ao servidor RMI!");
        } catch (Exception e) {
            e.printStackTrace();
            rmiClient = null;
        }

        // Carrega o fundo da Fase 1
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao.jpeg");
        if (backgroundTexture == null) {
            throw new RuntimeException("Erro: textura bueirao.jpeg não encontrada!");
        }
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        // Cria os blocos e escadas PARA O NÍVEL 1
        blocks = new Blocks(1);
        ladders = new Ladders(1);

        // Passa os blocos e escadas para o Player (player local)
        player = new Player();
        player.setLevelDependencies(blocks, ladders);
        player.addObserver(this); // Registra como observador

        // Cria inimigos PARA O NÍVEL 1
        Enemy.createEnemies(1);

        // Define a zona de vitória (baseado na sua imagem, perto da parede direita em x=2032)
        victoryZone = new Rectangle(2000, 50, 32, 172);

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);

        System.out.println("TelaNivel1 carregada!");
    }

    @Override
    public void render(float delta) {

        // 1) coletar input do jogador
        PlayerInput input = new PlayerInput();
        input.clientTimestamp = System.currentTimeMillis();
        input.left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        input.right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        input.jump = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        // se não estiver conectado, não tenta enviar nada
        if (rmiClient == null) {
            // modo offline (local)
            // ainda assim atualiza localmente e desenha
        } else {
            // 2) enviar input (pode rate-limit para reduzir chamadas)
            try {
                rmiClient.sendInput(input);
            } catch (Exception ex) {
                // log e seguir em modo local caso haja erro de rede
                ex.printStackTrace();
            }
        }

        // 3) receber e aplicar snapshots (thread-safe: pollSnapshot usa fila interna)
        GameSnapshot lastProcessed = null;
        if (rmiClient != null) {
            GameSnapshot snap;
            while ((snap = rmiClient.pollSnapshot()) != null) {
                // Aplicar imediatamente (estamos na thread de render — seguro)
                try {
                    applySnapshotOnRenderThread(snap);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                lastProcessed = snap;
            }
        }

        // armazenar último snapshot se necessário
        // (opcional: poderia usar para debug ou interpolação)
        // if (lastProcessed != null) this.lastSnap = lastProcessed;

        // 4) Lógica de update do jogador local
        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                if (gameOver) Gdx.app.exit();
            }
        }

        // processa ações de áudio enfileiradas por outras threads (RMI etc.)
        Runnable audioTask;
        while ((audioTask = audioActions.poll()) != null) {
            try {
                audioTask.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        AssetsManager.getInstance().update(delta);

        // Lógica de colisão com inimigos
        if (!gameOver && !gameWon && player.isAlive()) {
            for (int i = 0; i < Enemy.enemies.size; i++) {
                Enemy enemy = Enemy.enemies.get(i);
                enemy.update(delta); // Passa o delta para o update do inimigo

                if (player.isAttacking() && player.collide(enemy)) {
                    enemy.takeDamage(1);
                    if (!enemy.isAlive()) {
                        Enemy.enemies.removeIndex(i);
                        i--;
                        player.addScore(100);
                    }
                } else if (player.collide(enemy)) {
                    player.tryTakeDamage(0.5f);
                }
            }

            if (player.getBounds().overlaps(victoryZone)) {
                Gdx.app.log("TelaNivel1", "Jogador alcançou a saida!");
                gameWon = true; // Trava o jogador
                jogo.goToWinScreen(2); // Vai para a tela de transição
                return; // Para a execução deste frame
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

        // Atualiza projectionMatrix a cada frame (já foi atualizado pela câmera)
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);

        // Desenha blocos
        blocks.draw(batch);

        // Desenha jogador local
        player.draw(batch);

        // Desenha jogadores remotos
        for (Player p : players.values()) {
            if (p == player) continue;
            p.draw(batch);
        }

        // Desenha inimigos
        Enemy.drawAll(batch);

        // HUD / Mensagens
        if (gameOver) {
            String text = "VOCÊ PERDEU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        } else if (gameWon) {
            String text = "VOCÊ GANHOU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        }

        font.draw(batch, "Vida: " + player.getHealth(), camera.position.x - 500, camera.viewportHeight - 10);
        font.draw(batch, "Pontuação: " + player.getScore(), camera.position.x - 400, camera.viewportHeight - 10);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override
    public void pause() {
        // nada
    }

    @Override
    public void resume() {
        // nada
    }

    @Override
    public void hide() {
        // nada
    }

    @Override
    public void dispose() {
        Enemy.disposeAll();
        font.dispose();
        fontGameOver.dispose();
    }

    // --- Implementação dos métodos do Observer ---
    @Override
    public void onPlayerDamaged(float newHealth) {
        Gdx.app.log("OBSERVADOR", "O jogador tomou dano. Vida: " + newHealth);
    }

    @Override
    public void onPlayerAttack() {
        Gdx.app.log("OBSERVADOR", "O jogador atacou!");
    }

    @Override
    public void onScoreChanged(int newScore) {
        Gdx.app.log("OBSERVADOR", "Nova pontuação: " + newScore);
        if (newScore >= 700 && !gameWon && player.isAlive()) {
            gameWon = true;
            gameOverTimer = 0f;
            jogo.goToWinScreen(2);
            Gdx.app.log("JOGO", "Parabéns, você ganhou por pontos!");
        }
    }

    @Override
    public void onPlayerDied() {
        Gdx.app.log("OBSERVADOR", "Jogador morreu!");
        gameOver = true;
        gameOverTimer = 0f;
    }

    /**
     * Aplica um snapshot já na thread de render (nunca toca AssetsManager aqui).
     * Deve apenas atualizar dados dos players e criar ghosts leves se necessário.
     */
    private void applySnapshotOnRenderThread(GameSnapshot snap) {
        if (snap == null || snap.players == null) return;

        for (java.util.Map.Entry<java.util.UUID, PlayerState> e : snap.players.entrySet()) {
            java.util.UUID id = e.getKey();
            PlayerState ps = e.getValue();

            // Ignora se for o player local (caso local também esteja no map de snapshot)
            // O jogador local é controlado pelo input local — mas você pode reconciliar se quiser.
            // Se tiver um localPlayerId, compare aqui e ignore.
            Player local = players.get(id);
            if (local == null) {
                // cria um ghost leve (factory deve garantir que NÃO carrega texturas)
                Player ghost = Player.createNetworkGhost(ps.x, ps.y, this.blocks, this.ladders);
                ghost.setName(ps.name);
                players.put(id, ghost);
                continue;
            }

            // atualiza apenas dados puros (sem tocar GL/Assets)
            local.setPosition(ps.x, ps.y);
            local.setHp(ps.hp);
            local.setFacing(ps.facingRight);
        }

        // remove players que sumiram do snapshot
        java.util.Set<java.util.UUID> serverIds = snap.players.keySet();
        java.util.List<java.util.UUID> toRemove = new java.util.ArrayList<>();
        for (java.util.UUID localId : players.keySet()) {
            if (!serverIds.contains(localId)) {
                toRemove.add(localId);
            }
        }
        for (java.util.UUID rid : toRemove) {
            players.remove(rid);
        }
    }

    // --- método utilitário que o resto do código (listener RMI) pode usar ---
    // Exemplo de uso (no listener): TelaNivel1.enqueueAudio(() -> AssetsManager.getInstance().getSound("pulo").play());
}
