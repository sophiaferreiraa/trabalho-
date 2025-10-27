package gpjecc.blogspot.com.GameObjects.Base;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import gpjecc.blogspot.com.AssetsManager.AssetsManager;

public class GameObject {
    protected Vector2 position;
    protected Vector2 size;

    protected Texture texture;

    protected TextureRegion[][] animationFrames;
    protected Animation animation;
    protected TextureRegion currentFrame;
    
    public void setCurrentFrame(TextureRegion currentFrame) {
        this.currentFrame = currentFrame;
    }
    
    public GameObject(Vector2 position, Vector2 size, String texture) {
        this.position = position;
        this.size = size;

        this.animationFrames = AssetsManager.getInstance().getFrames(texture, this.size);
        this.animation = AssetsManager.getInstance().getAnimation(animationFrames, 0, 0.15f);
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);

        this.texture = AssetsManager.getInstance().getTexture(texture);
    }

    public void update(){
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(this.currentFrame, this.position.x, this.position.y, (int)this.size.x, (int)this.size.y);
    }
    
    public boolean outOfMap(){
        if(this.position.x <= 28 || this.position.x + this.size.x >= 2012){
            return true;
        } else if(this.position.y <= 50 || this.position.y + this.size.y >= 210){
            return true;
        }

        return false;
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public Vector2 getSize() {
        return this.size;
    }
    
    public TextureRegion getCurrentFrame() {
        return currentFrame;
    }

    public boolean collide(GameObject otherObj){
        return this.collide(otherObj.getPosition(), otherObj.getSize());
    }

    public boolean collide(Vector2 otherPos, Vector2 otherSize){
        if(
            otherPos.x < this.position.x + this.size.x &&
            otherPos.x + otherSize.x > this.position.x &&
            otherPos.y < this.position.y + this.size.y &&
            otherPos.y + otherSize.y > this.position.y
        ){
            return true;
        }

        return false;
    }

    public void dispose() {
        this.texture.dispose();
    }
}
