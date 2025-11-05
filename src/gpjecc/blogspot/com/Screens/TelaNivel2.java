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

// AVISO: ESTA TELA NÃO ESTÁ SINCRONIZADA COM A REDE
public class TelaNivel2 implements Screen, PlayerObserver {

    private final TartarugasJogo jogo;
    private OrthographicCamera camera;
    private BitmapFont font;
    private SpriteBatch batch; // Adicionado

    public TelaNivel2(TartarugasJogo jogo) {
        this.jogo = jogo;
        this.batch = jogo.getBatch(); // Pega o batch principal
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        camera.update();
        font = new BitmapFont();
        font.setScale(1.5f);
    }

    @Override
    public void show() {
        Gdx.app.log("TelaNivel2", "TelaNivel2 não está implementada para multiplayer.");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Usa o batch principal
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        font.draw(batch, "Nivel 2 (Nao implementado para Multiplayer)", Gdx.graphics.getWidth() / 2f - 150, Gdx.graphics.getHeight() / 2f);
        font.draw(batch, "Clique para voltar ao Menu", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 30);
        batch.end();
        
        if(Gdx.input.justTouched()) {
            jogo.goToMenu(); // Volta ao menu se tocar na tela
        }
    }

    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        font.dispose();
        // Não dá dispose no batch
    }

    // --- Observer ---
    @Override public void onPlayerDamaged(float newHealth) { }
    @Override public void onPlayerAttack() { }
    @Override public void onScoreChanged(int newScore) { }
    @Override public void onPlayerDied() { }
}