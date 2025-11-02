package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.math.Rectangle;

public class LadderBlock implements Collidable {
    private Rectangle bounds;
    private float topY; // Posição Y final no topo da escada

    public LadderBlock(float x, float y, float width, float height, float topY) {
        this.bounds = new Rectangle(x, y, width, height);
        this.topY = topY;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    public float getTopY() {
        return topY;
    }
}
