package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

import gpjecc.blogspot.com.TartarugasJogo;
import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.GameObjects.Enemy;
import gpjecc.blogspot.com.GameObjects.Player;

import gpjecc.blogspot.com.Observers.PlayerObserver;

public class TelaInicial implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player;

    private OrthographicCamera camera;
    private float backgroundWidth;
    private float backgroundHeight;

    private BitmapFont font;          // Fonte padrão
    private BitmapFont fontGameOver;  // Fonte para mensagens de fim de jogo

    private boolean gameOver = false;
    private boolean gameWon = false;      // Flag para vitória
    private float gameOverTimer = 0f;

    public TelaInicial(TartarugasJogo jogo) {
        this.jogo = jogo;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.local("bueirao.jpeg"));
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        player = new Player();
        player.addObserver(this); // Registra como observador

        Enemy.createDefaultEnemies();

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);  // Escala maior para mensagens

        System.out.println("TelaInicial carregada!");
    }

    @Override
    public void render(float delta) {
        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                Gdx.app.exit();
            }
        }

        AssetsManager.getInstance().update(delta);

        if (!gameOver && !gameWon) {
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
                    player.tryTakeDamage(0.5f);
                }
            }
        }

        // Configura a câmera seguindo o jogador
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
        player.draw(batch);
        Enemy.drawAll(batch);

        if (gameOver) {
            // Centralizar e desenhar "VOCÊ PERDEU"
            String text = "VOCÊ PERDEU!!";
            float textWidth = fontGameOver.getSpaceWidth() * text.length() * fontGameOver.getData().scaleX;
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;  // Pequeno ajuste vertical
            fontGameOver.draw(batch, text, x, y);
        } else if (gameWon) {
            // Centralizar e desenhar "VOCÊ GANHOU"
            String text = "VOCÊ GANHOU!!";
            float textWidth = fontGameOver.getSpaceWidth() * text.length() * fontGameOver.getData().scaleX;
            float x = camera.position.x - textWidth / 2f;
            float y = camera.viewportHeight / 2f + 20;
            fontGameOver.draw(batch, text, x, y);
        }

        // Desenha HUD com a fonte normal
        font.draw(batch, "Vida: " + player.getHealth(), camera.position.x - 500, camera.viewportHeight - 10);
        font.draw(batch, "Pontuação: " + player.getScore(), camera.position.x - 400, camera.viewportHeight - 10);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        AssetsManager.getInstance().dispose();
        backgroundTexture.dispose();
        player.dispose();
        Enemy.disposeAll();
        font.dispose();
        fontGameOver.dispose();
    }

    // Implementação dos métodos do Observer ---------------------------------------

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
        if (newScore >= 700 && !gameWon) {
            gameWon = true;
            gameOverTimer = 0f;  // reinicia o timer para fechar o jogo depois de 5 segundos
            Gdx.app.log("JOGO", "Parabéns, você ganhou!");
        }
    }

    @Override
    public void onPlayerDied() {
        Gdx.app.log("OBSERVADOR", "Jogador morreu!");
        gameOver = true;
        gameOverTimer = 0f;
    }
}
