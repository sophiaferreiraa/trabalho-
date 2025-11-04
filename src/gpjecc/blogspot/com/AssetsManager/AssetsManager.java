package gpjecc.blogspot.com.AssetsManager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class AssetsManager extends ApplicationAdapter {
    private static AssetsManager INSTANCE = null;
    private static float stateTime;

    private AssetManager assetManager;

    private Music musicaFundo; // campo para armazenar a música

    private AssetsManager() {
        this.create();
    }

    @Override
    public void create() {
        assetManager = new AssetManager();
        stateTime = 0f;

        // Carrega texturas
        assetManager.load("assets/spriteTartaruga.png", Texture.class);
        assetManager.load("assets/bicho.png", Texture.class);
        assetManager.load("assets/inimigo_Fase1migo.png", Texture.class);
        assetManager.load("assets/red.png", Texture.class); // Carrega o bloco da Fase 1

        // --- NOVOS ASSETS ---
        assetManager.load("assets/bueirao.jpeg", Texture.class); // Fundo Fase 2
        assetManager.load("assets/bicho.png", Texture.class); // Inimigo Fase 2
        assetManager.load("assets/red.png", Texture.class); // Bloco Fase 2
        assetManager.load("assets/bueirao_fase2.jpeg", Texture.class); 
        assetManager.load("assets/blue.jpeg", Texture.class); // Bloco Fase 2
        //assetManager.load("blue.png", Texture.class);
        assetManager.load("assets/inimigo_fase2.png", Texture.class);
        assetManager.load("assets/Robot_enemy.png", Texture.class);


        
        // ---------------------

        assetManager.finishLoading();

        // Carrega e toca música
        musicaFundo = Gdx.audio.newMusic(Gdx.files.internal("assets/2019-12-11_-_Retro_Platforming_-_David_Fesliyan.mp3"));
        musicaFundo.setLooping(true);
        musicaFundo.setVolume(0.5f);
        musicaFundo.play(); // Música começa a tocar ao iniciar o jogo
    }

    public void update(float deltaTime) {
        // Atualiza o tempo de animação
        stateTime += deltaTime;
    }

    public Texture getTexture(String key) {
        // Verifica se a chave já contém "assets/"
        String path = key.startsWith("assets/") ? key : "assets/" + key;
        
        // Verifica se a chave já contém .png
        if (!path.endsWith(".png") && !path.endsWith(".jpeg")) {
             // Tenta adivinhar a extensão
            if (assetManager.isLoaded(path + ".png")) {
                path += ".png";
            } else if (assetManager.isLoaded(path + ".jpeg")) {
                path += ".jpeg";
            } else {
                 // Fallback para .png se não encontrar
                Gdx.app.log("AssetsManager", "Assumindo .png para: " + key);
                path = key.startsWith("assets/") ? key : "assets/" + key + ".png";
            }
        }
        
        // System.out.println("Tentando carregar: " + path);
        return this.assetManager.get(path, Texture.class);
    }

    public TextureRegion[][] getFrames(String key, Vector2 size) {
        //return TextureRegion.split(this.getTexture(key), (int) size.x, (int) size.y);
         TextureRegion[][] frames = TextureRegion.split(this.getTexture(key), (int) size.x, (int) size.y);
    System.out.println("Frames para " + key + ": linhas = " + frames.length +
        ", colunas = " + (frames.length > 0 ? frames[0].length : 0));
    return frames;
    }

    public Animation getAnimation(TextureRegion[][] frames, int line, float frameDuration) {
        return new Animation(frameDuration, frames[line]);
    }

    public TextureRegion getCurrentFrame(Animation animation) {
        return animation.getKeyFrame(AssetsManager.stateTime, true);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        if (musicaFundo != null) {
            musicaFundo.dispose();
        }
    }

    public static AssetsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AssetsManager();
        }

        return INSTANCE;
    }
}