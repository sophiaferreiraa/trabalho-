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

    private AssetsManager(){
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
        //System.out.println(assetManager.getAssetNames());
        return this.assetManager.get("assets/" + key + ".png", Texture.class);
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

    @Override
    public void dispose() {
    assetManager.dispose();
    if (musicaFundo != null) {
        musicaFundo.dispose();
    }
}

    public static AssetsManager getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AssetsManager();
        }

        return INSTANCE;
    }
}
