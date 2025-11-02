package gpjecc.blogspot.com;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tartarugas Game";
        config.width = 1024;
        config.height = 256;
        new LwjglApplication(new TartarugasJogo(), config);
    }
}
