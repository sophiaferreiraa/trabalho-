// sophiaferreiraa/trabalho-/trabalho--0534c1e57f69de4e39b35eda93a3dec3369520db/src/gpjecc/blogspot/com/GameObjects/Blocks/TileBlock.java
package gpjecc.blogspot.com.GameObjects.Blocks;

// import com.badlogic.gdx.Gdx; // REMOVIDO (não estava sendo usado)
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import gpjecc.blogspot.com.AssetsManager.AssetsManager; 

public class TileBlock {
    private Rectangle bounds;
    private Texture texture;  

    public TileBlock(float x, float y, float width, float height) {
        this(x, y, width, height, "red"); 
    }

    public TileBlock(float x, float y, float width, float height, String textureKey) {
        this.bounds = new Rectangle(x, y, width, height);
        
        if (textureKey != null && !textureKey.isEmpty()) {
            this.texture = AssetsManager.getInstance().getTexture(textureKey);
        } else {
            this.texture = AssetsManager.getInstance().getTexture("red");
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}