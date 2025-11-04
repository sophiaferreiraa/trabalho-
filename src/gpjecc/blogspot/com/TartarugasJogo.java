package gpjecc.blogspot.com;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen; // <-- IMPORT PRINCIPAL ADICIONADO
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import gpjecc.blogspot.com.AssetsManager.AssetsManager;
// import gpjecc.blogspot.com.Screens.TelaInicial; // <-- IMPORT ANTIGO REMOVIDO
import gpjecc.blogspot.com.Screens.TelaNivel1;
import gpjecc.blogspot.com.Screens.TelaNivel2;
import gpjecc.blogspot.com.Screens.TelaVitoria;

public class TartarugasJogo extends Game {

    private SpriteBatch batch;
    
    // Mantém as telas para limpar a memória
    private Screen currentScreen; // Agora "Screen" será reconhecido

    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        // Inicia o carregamento de assets e música
        AssetsManager.getInstance();

        // Inicia no Nível 1
        goToLevel1();
    }

    // --- NOVOS MÉTODOS DE NAVEGAÇÃO ---
    
    private void changeScreen(Screen newScreen) { // Agora "Screen" será reconhecido
        // Se já tínhamos uma tela, limpa ela
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        currentScreen = newScreen;
        setScreen(currentScreen);
    }
    
    public void goToLevel1() {
        Gdx.app.log("TartarugasJogo", "Indo para Nível 1");
        changeScreen(new TelaNivel1(this)); 
    }

    public void goToWinScreen(int nextLevel) {
        Gdx.app.log("TartarugasJogo", "Indo para Tela de Vitoria (prox: " + nextLevel + ")");
        changeScreen(new TelaVitoria(this, nextLevel));
    }

    public void goToLevel2() {
        Gdx.app.log("TartarugasJogo", "Indo para Nível 2");
        changeScreen(new TelaNivel2(this));
    }
    // ---------------------------------

    @Override
    public void render() {
        super.render(); // Chama o render da tela atual
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        AssetsManager.getInstance().dispose(); // Dispose do AssetsManager
    }
}