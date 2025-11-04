package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
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
import gpjecc.blogspot.com.GameObjects.RobotEnemy;

public class TelaNivel2 implements Screen, PlayerObserver {

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

    // Controle do tempo e progresso do jogador
    private float tempoDesdeInicio = 0f;

    public TelaNivel2(TartarugasJogo jogo) {
        this.jogo = jogo;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
    }

    @Override
    public void show() {
        backgroundTexture = AssetsManager.getInstance().getTexture("bueirao_fase2.jpeg");
        backgroundWidth = backgroundTexture.getWidth();
        backgroundHeight = backgroundTexture.getHeight();

        blocks = new Blocks(2);
        ladders = new Ladders(2);

        player = new Player();
        player.setLevelDependencies(blocks, ladders);
        player.addObserver(this);

        Enemy.createEnemies(2);
        RobotEnemy.createRobots(2);

        // 🔹 Move a zona de vitória bem mais para o final
        victoryZone = new Rectangle(backgroundWidth - 50, 50, 40, 172);

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);

        System.out.println("TelaNivel2 carregada!");
    }

    @Override
    public void render(float delta) {
        tempoDesdeInicio += delta;

        if (!gameOver && !gameWon && player.isAlive()) {
            player.update(delta);
        } else if (gameOver || gameWon) {
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                Gdx.app.exit();
            }
        }

        AssetsManager.getInstance().update(delta);

        if (!gameOver && !gameWon && player.isAlive()) {
            // --- Atualiza inimigos comuns ---
            for (int i = 0; i < Enemy.enemies.size; i++) {
                Enemy enemy = Enemy.enemies.get(i);
                enemy.update(delta);

                if (player.isAttacking() && player.collide(enemy)) {
                    enemy.takeDamage(1);
                    if (!enemy.isAlive()) {
                        Enemy.enemies.removeIndex(i);
                        i--;
                        player.addScore(200);
                    }
                } else if (player.collide(enemy)) {
                    player.tryTakeDamage(0.5f);
                }
            }

            // --- Atualiza inimigos robôs ---
            for (int i = 0; i < RobotEnemy.robots.size; i++) {
                RobotEnemy robot = RobotEnemy.robots.get(i);
                robot.update(delta);

                if (player.isAttacking() && player.collide(robot)) {
                    robot.takeDamage(1);
                    if (!robot.isAlive()) {
                        RobotEnemy.robots.removeIndex(i);
                        i--;
                        player.addScore(300);
                    }
                } else if (player.collide(robot)) {
                    player.tryTakeDamage(0.5f);
                }
            }

            // 🔹 Só ativa vitória depois de andar bastante e o tempo passar
            if (tempoDesdeInicio > 3f && player.getPosition().x > backgroundWidth - 150 && player.getBounds().overlaps(victoryZone)) {
                Gdx.app.log("TelaNivel2", "Jogador ZEROU O JOGO!");
                gameWon = true;
                gameOverTimer = 0f;
                return;
            }
        }

        // --- Câmera ---
        camera.position.x = player.getPosition().x;
        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        camera.position.x = Math.max(effectiveViewportWidth / 2, camera.position.x);
        float maxCameraX = Math.max(effectiveViewportWidth / 2, backgroundWidth - effectiveViewportWidth / 2);
        camera.position.x = Math.min(maxCameraX, camera.position.x);
        camera.update();

        // --- Renderização ---
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = jogo.getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);

        blocks.draw(batch);
        player.draw(batch);
        Enemy.drawAll(batch);
        RobotEnemy.drawAll(batch);

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

    @Override public void resize(int width, int height) { camera.setToOrtho(false, 1024, 256); camera.update(); }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        Enemy.disposeAll();
        RobotEnemy.disposeAll();
        font.dispose();
        fontGameOver.dispose();
    }

    // --- Observer ---
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
    }

    @Override
    public void onPlayerDied() {
        Gdx.app.log("OBSERVADOR", "Jogador morreu!");
        gameOver = true;
        gameOverTimer = 0f;
    }
}
