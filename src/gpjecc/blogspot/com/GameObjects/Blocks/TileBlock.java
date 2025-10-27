package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class TileBlock {
    private Rectangle bounds;
    private static Texture texture;  // Reuso da textura para evitar carregar várias vezes

    public TileBlock(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        if (texture == null) {
            texture = new Texture(Gdx.files.internal("assets/red.png"));
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
