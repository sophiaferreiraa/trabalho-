// sophiaferreiraa/trabalho-/trabalho--02347792bae4ea97c472a8e845b9c8792d2d1ef5/src/gpjecc/blogspot/com/AssetsManager/AssetsManager.java
package gpjecc.blogspot.com.AssetsManager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class AssetsManager {
    private static AssetsManager INSTANCE = null;
    private static float stateTime = 0f;

    private AssetManager assetManager;
    private Music musicaFundo; // campo para armazenar a música

    private AssetsManager() {
        create();
    }

    public void create() {
        assetManager = new AssetManager();
        stateTime = 0f;

        // --- CARREGA TEXTURAS (Fase 1) ---
        assetManager.load("spriteTartaruga.png", Texture.class);
        assetManager.load("bicho.png", Texture.class);
        assetManager.load("inimigo_Fase1migo.png", Texture.class);
        assetManager.load("red.png", Texture.class);
        assetManager.load("bueirao.jpeg", Texture.class); // Fundo Fase 1

        // --- NOVOS ASSETS (Fase 2) ---
        assetManager.load("bueirao_fase2.jpeg", Texture.class); // Fundo Fase 2
        assetManager.load("inimigo_fase2.png", Texture.class);  // <-- ADICIONADO
        assetManager.load("blue.jpeg", Texture.class);           // <-- ADICIONADO
        // ---------------------------------

        // Carrega também a música via AssetManager
        assetManager.load("2019-12-11_-_Retro_Platforming_-_David_Fesliyan.mp3", Music.class);

        assetManager.finishLoading(); // Carrega tudo agora

        // Toca a música
        if (assetManager.isLoaded("2019-12-11_-_Retro_Platforming_-_David_Fesliyan.mp3", Music.class)) {
            musicaFundo = assetManager.get("2019-12-11_-_Retro_Platforming_-_David_Fesliyan.mp3", Music.class);
            musicaFundo.setLooping(true);
            musicaFundo.setVolume(0.5f);
            musicaFundo.play();
        } else {
            Gdx.app.log("AssetsManager", "Música não carregada pelo AssetManager.");
        }
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
    }

    // O resto do seu arquivo AssetsManager.java (getTexture, getFrames, etc.)
    // pode permanecer exatamente como está.
    // ... (cole o resto do seu arquivo original aqui) ...
    
    /**
     * getTexture tolerante:
     * - aceita key com/sem "assets/"
     * - aceita key com/sem extensão (.png, .jpg, .jpeg)
     * - tenta carregar se o arquivo existir mas ainda não foi carregado
     */
    public Texture getTexture(String key) {
        if (key == null) throw new IllegalArgumentException("getTexture: key == null");

        // normaliza
        String normalized = key.trim();
        if (normalized.startsWith("assets/")) {
            normalized = normalized.substring("assets/".length());
        }
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        // se já tem extensão, tenta direto
        if (normalized.endsWith(".png") || normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) {
            return getOrLoad(normalized);
        }

        // tenta com extensões comuns
        String[] exts = new String[]{".png", ".jpeg", ".jpg"};
        for (String ext : exts) {
            String attempt = normalized + ext;
            if (assetManager.isLoaded(attempt, Texture.class)) {
                return assetManager.get(attempt, Texture.class);
            }
            // se o arquivo existe no runtime, carrega como fallback
            if (Gdx.files.internal(attempt).exists()) {
                Gdx.app.log("AssetsManager", "Carregando (fallback) texture: " + attempt);
                assetManager.load(attempt, Texture.class);
                assetManager.finishLoading();
                if (assetManager.isLoaded(attempt, Texture.class)) {
                    return assetManager.get(attempt, Texture.class);
                }
            }
        }

        // último recurso: tentar sem extensão (pode lançar erro se não existir)
        return getOrLoad(normalized);
    }

    private Texture getOrLoad(String path) {
        if (assetManager.isLoaded(path, Texture.class)) {
            return assetManager.get(path, Texture.class);
        }
        // tenta carregar se o arquivo existir no runtime
        if (Gdx.files.internal(path).exists()) {
            Gdx.app.log("AssetsManager", "Carregando texture: " + path);
            assetManager.load(path, Texture.class);
            assetManager.finishLoading();
            if (assetManager.isLoaded(path, Texture.class)) {
                return assetManager.get(path, Texture.class);
            }
        }
        // erro claro se não encontrado
        throw new GdxRuntimeException("Asset not loaded (nem encontrado): " + path + " — verifique android/assets e nomes/maiusculas.");
    }

    public TextureRegion[][] getFrames(String key, Vector2 size) {
        return TextureRegion.split(this.getTexture(key), (int) size.x, (int) size.y);
    }

    public Animation getAnimation(TextureRegion[][] frames, int line, float frameDuration) {
        return new Animation(frameDuration, frames[line]);
    }

    public TextureRegion getCurrentFrame(Animation animation) {
        return animation.getKeyFrame(AssetsManager.stateTime, true);
    }

    public void dispose() {
        if (assetManager != null) assetManager.dispose();
        if (musicaFundo != null) musicaFundo.dispose();
    }

    public static AssetsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AssetsManager();
        }
        return INSTANCE;
    }
}