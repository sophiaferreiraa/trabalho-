// sophiaferreiraa/trabalho-/trabalho--02347792bae4ea97c472a8e845b9c8792d2d1ef5/src/gpjecc/blogspot/com/Screens/TelaNivel2.java
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

public class TelaNivel2 implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private Texture backgroundTexture;
    private Player player;

    private Blocks blocks;
    private Ladders ladders;
    // private Rectangle victoryZone; // REMOVIDO POR ENQUANTO

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

        // --- MUDANÇA: CRIA NÍVEL VAZIO ---
        // Passamos -1 para os construtores não carregarem nada (graças à nossa mudança)
        blocks = new Blocks(-1);
        ladders = new Ladders(-1);
        // --- FIM DA MUDANÇA ---

        // Passa os blocos e escadas vazios para o Player
        player = new Player(blocks, ladders);
        player.addObserver(this); 
        
        // --- MUDANÇA: POSICIONA O JOGADOR ---
        player.getPosition().set(100, 60); // Põe o jogador visível no chão
        // --- FIM DA MUDANÇA ---

        // --- MUDANÇA: INIMIGOS DESLIGADOS ---
        // Enemy.createEnemies(2); // COMENTADO
        // --- FIM DA MUDANÇA ---

        // --- MUDANÇA: ZONA DE VITÓRIA DESLIGADA ---
        // victoryZone = new Rectangle(980, 50, 28, 172); // COMENTADO
        // --- FIM DA MUDANÇA ---

        font = new BitmapFont();
        fontGameOver = new BitmapFont();
        fontGameOver.setScale(2f);

        System.out.println("TelaNivel2 (Modo de Teste) carregada!");
    }

    @Override
    public void render(float delta) {
        // --- MUDANÇA: FÍSICA E ANIMAÇÃO DESLIGADAS ---
        // if (!gameOver && !gameWon && player.isAlive()) {
        //     player.update(delta); // COMENTADO (desliga gravidade, controle, colisões)
        // } 
        if (gameOver || gameWon) { // Apenas lógica de game over/won
            gameOverTimer += delta;
            if (gameOverTimer >= 5f) {
                Gdx.app.exit();
            }
        }

        // AssetsManager.getInstance().update(delta); // COMENTADO (pausa as animações)
        // --- FIM DA MUDANÇA ---


        // --- MUDANÇA: LÓGICA DE INIMIGOS E VITÓRIA DESLIGADA ---
        // if (!gameOver && !gameWon && player.isAlive()) {
        //     ... (toda a lógica de inimigos foi removida) ...
            
        //     ... (toda a lógica de vitória foi removida) ...
        // }
        // --- FIM DA MUDANÇA ---


        // Câmera (mantida, mas não vai seguir o jogador)
        // Você pode querer centralizar a câmera manualmente se o jogador não se mover
        // camera.position.x = player.getPosition().x; // Comentado
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        SpriteBatch batch = jogo.getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Desenha o fundo
        batch.draw(backgroundTexture, 0, 0, backgroundWidth, backgroundHeight);
        
        // blocks.draw(batch); // Não desenha blocos (lista está vazia)
        
        // Desenha o jogador
        player.draw(batch);
        
        // Enemy.drawAll(batch); // Não desenha inimigos (lista está vazia)

        // Lógica de Game Over (mantida caso o jogador morra por outro motivo)
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

        // HUD (mantido)
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

    // --- Métodos do Observer (mantidos) ---
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