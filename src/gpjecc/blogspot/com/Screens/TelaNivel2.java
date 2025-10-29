// sophiaferreiraa/trabalho-/trabalho--0534c1e57f69de4e39b35eda93a3dec3369520db/src/gpjecc/blogspot/com/Screens/TelaNivel2.java
package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; // <-- IMPORT ADICIONADO
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

public class TelaNivel2 implements Screen, PlayerObserver { // Agora "Screen" será reconhecido

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player;

    private Blocks blocks;
    private Ladders ladders;
    private Rectangle victoryZone; // Gatilho para vencer o Nível 2

    private OrthographicCamera camera;
    private float backgroundWidth;
    private float backgroundHeight;

    private BitmapFont font;
    private BitmapFont fontGameOver;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private float gameOverTimer = 0f;

    public TelaNivel2(TartarugasJogo jogo) {
        this.jogo = jogo;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override
    public void show() {
        // --- CARREGA ASSETS DO NÍVEL 2 ---
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao_fase2.jpeg");
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        // Cria os blocos e escadas PARA O NÍVEL 2
        blocks = new Blocks(2);
        ladders = new Ladders(2);

        // Passa os blocos e escadas para o Player
        player = new Player(blocks, ladders);
        player.addObserver(this); 

        // Cria inimigos PARA O NÍVEL 2
        Enemy.createEnemies(2);

        // Zona de vitória para o Nível 2 (perto da parede em x=1008)
        victoryZone = new Rectangle(980, 50, 28, 172); 

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f); // <-- CORREÇÃO do setScale (sem .getData())

        System.out.println("TelaNivel2 carregada!");
    }

    @Override
    public void render(float delta) {
        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                // Se perder OU GANHAR (por ser o último nível), sai do jogo.
                Gdx.app.exit();
            }
        }

        AssetsManager.getInstance().update(delta);

        if (!gameOver && !gameWon && player.isAlive()) {
            for (int i = 0; i < Enemy.enemies.size; i++) {
                Enemy enemy = Enemy.enemies.get(i);
                enemy.update(delta); // Passa o delta para o update do inimigo

                if (player.isAttacking() && player.collide(enemy)) {
                    enemy.takeDamage(1);
                    if (!enemy.isAlive()) {
                        Enemy.enemies.removeIndex(i);
                        i--;
                        player.addScore(200); // Mais pontos no Nível 2
                    }
                } else if (player.collide(enemy)) {
                    player.tryTakeDamage(0.5f);
                }
            }
            
            // --- VERIFICAÇÃO DE VITÓRIA (NÍVEL 2) ---
            if (player.getBounds().overlaps(victoryZone)) {
                Gdx.app.log("TelaNivel2", "Jogador ZEROU O JOGO!");
                gameWon = true; // Ativa a tela de "VOCÊ GANHOU"
                gameOverTimer = 0f; // Inicia o timer para fechar o jogo
                return; 
            }
        }

        // Câmera
        camera.position.x = player.getPosition().x;
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        camera.position.x = Math.max(effectiveViewportWidth / 2, camera.position.x);
        // Limita a câmera ao tamanho do fundo (que pode ser menor agora)
        float maxCameraX = Math.max(effectiveViewportWidth / 2, backgroundWidth - effectiveViewportWidth / 2);
        camera.position.x = Math.min(maxCameraX, camera.position.x);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = jogo.getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);
        
        blocks.draw(batch); // Desenha blocos do Nível 2
        player.draw(batch);
        Enemy.drawAll(batch); // Desenha inimigos do Nível 2

        if (gameOver) {
            String text = "VOCÊ PERDEU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        } else if (gameWon) {
            // Venceu o Nível 2 (Fim de Jogo)
            String text = "VOCÊ GANHOU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX();
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        }

        // HUD
        font.draw(batch, "Vida: " + player.getHealth(), camera.position.x - 500, camera.viewportHeight - 10);
        font.draw(batch, "Pontuação: " + player.getScore(), camera.position.x - 400, camera.viewportHeight - 10);

        batch.end();
    }

    @Override public void resize(int width, int height) { camera.setToOrtho(false, 1024, 256); camera.update(); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

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
        // Sem vitória por pontos no Nível 2 (só por chegar ao fim)
    }

    @Override
    public void onPlayerDied() {
        Gdx.app.log("OBSERVADOR", "Jogador morreu!");
        gameOver = true;
        gameOverTimer = 0f;
    }
}