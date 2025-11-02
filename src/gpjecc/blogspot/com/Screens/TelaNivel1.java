// sophiaferreiraa/trabalho-/trabalho--0534c1e57f69de4e39b35eda93a3dec3369520db/src/gpjecc/blogspot/com/Screens/TelaNivel1.java
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

public class TelaNivel1 implements Screen, PlayerObserver { // Agora "Screen" será reconhecido

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player;

    // Instâncias de nível
    private Blocks blocks;
    private Ladders ladders;
    private Rectangle victoryZone; // Gatilho para vencer

    private OrthographicCamera camera;
    private float backgroundWidth;
    private float backgroundHeight;

    private BitmapFont font;
    private BitmapFont fontGameOver;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private float gameOverTimer = 0f;

    public TelaNivel1(TartarugasJogo jogo) {
        this.jogo = jogo;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override
    public void show() {
        // Carrega o fundo da Fase 1
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao.jpeg");
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        // Cria os blocos e escadas PARA O NÍVEL 1
        blocks = new Blocks(1);
        ladders = new Ladders(1);

        // Passa os blocos e escadas para o Player
        player = new Player(blocks, ladders);
        player.addObserver(this); // Registra como observador

        // Cria inimigos PARA O NÍVEL 1
        Enemy.createEnemies(1);

        // Define a zona de vitória (baseado na sua imagem, perto da parede direita em x=2032)
        victoryZone = new Rectangle(2000, 50, 32, 172); 

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f); // <-- CORREÇÃO do setScale (sem .getData())

        System.out.println("TelaNivel1 carregada!");
    }

    @Override
    public void render(float delta) {
        // Lógica de update
        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                // Se perder, sai. Se ganhar, a tela de vitória já foi chamada.
                if (gameOver) Gdx.app.exit();
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
            
            // --- NOVA VERIFICAÇÃO DE VITÓRIA ---
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = jogo.getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);
        
        blocks.draw(batch); 

        player.draw(batch);
        Enemy.drawAll(batch);

        if (gameOver) {
            String text = "VOCÊ PERDEU!!";
            float textWidth = 15 * text.length() * fontGameOver.getScaleX(); // Cálculo de largura melhorado
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        } else if (player.getBounds().overlaps(victoryZone)) {
            // Este texto só aparece se o score for atingido (vitória alternativa)
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
}