package gpjecc.blogspot.com;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.Screens.TelaInicial;

public class TartarugasJogo extends Game {

    private SpriteBatch batch;

    public SpriteBatch getBatch() {
        return batch;
    }

   @Override
    public void create() {
    batch = new SpriteBatch();

    // Inicia o carregamento de assets e música
    AssetsManager.getInstance(); // Isso já chama o create() internamente, que toca a música

    setScreen(new TelaInicial(this));
}

    @Override
    public void render() {
        super.render(); // Chama o render da tela atual (TelaInicial)
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (getScreen() != null) {
            getScreen().dispose();
        }
    }
}