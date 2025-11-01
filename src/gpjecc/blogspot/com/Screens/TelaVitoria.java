// sophiaferreiraa/trabalho-/trabalho--0534c1e57f69de4e39b35eda93a3dec3369520db/src/gpjecc/blogspot/com/Screens/TelaVitoria.java
package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; // <-- IMPORT ADICIONADO
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import gpjecc.blogspot.com.TartarugasJogo;

public class TelaVitoria implements Screen { // Agora "Screen" será reconhecido

    private final TartarugasJogo jogo;
    private OrthographicCamera camera;
    private BitmapFont font;
    private float timer;
    private int nextLevel;

    public TelaVitoria(TartarugasJogo jogo, int nextLevel) {
        this.jogo = jogo;
        this.nextLevel = nextLevel;
        this.timer = 0f;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1024, 256);
        
        font = new BitmapFont();
        font.setScale(2f); // <-- CORREÇÃO do setScale (sem .getData())
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        timer += delta;

        // Após 4 segundos, avança para o próximo nível
        if (timer > 4.0f) {
            if (nextLevel == 2) {
                jogo.goToLevel2();
            }
            // (você pode adicionar 'else if (nextLevel == 3)' no futuro)
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        SpriteBatch batch = jogo.getBatch();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        String text = "FASE 1 COMPLETA!";
        String text2 = "Indo para a Fase " + nextLevel + "...";
        
        // Centraliza o texto (cálculo aproximado)
        float textWidth = 15 * text.length() * font.getScaleX(); // Cálculo de largura melhorado
        float textWidth2 = 15 * text2.length() * font.getScaleX();

        font.draw(batch, text, (camera.viewportWidth - textWidth) / 2, camera.viewportHeight / 2 + 20);
        font.draw(batch, text2, (camera.viewportWidth - textWidth2) / 2, camera.viewportHeight / 2 - 20);
        batch.end();
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        font.dispose();
    }
}