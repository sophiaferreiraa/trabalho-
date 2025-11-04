package gpjecc.blogspot.com;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {

    // Este método é chamado pelo TartarugasJogo para iniciar o LibGDX
    public static void initGame(TartarugasJogo jogo) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tartarugas Game";
        config.width = 1024;
        config.height = 256;
        new LwjglApplication(jogo, config);
    }

    //
    // --- ESTE É O NOVO PONTO DE ENTRADA ÚNICO ---
    //
    public static void main(String[] args) {
        // Apenas cria a classe principal do Jogo.
        // O jogo (em TelaMenu) cuidará da lógica de conexão.
        initGame(new TartarugasJogo());
    }
}