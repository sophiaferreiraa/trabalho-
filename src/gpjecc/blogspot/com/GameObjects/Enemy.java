package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import gpjecc.blogspot.com.GameObjects.Base.GameObject;

public class Enemy extends GameObject {

    // private int health = 3; // Estado agora é controlado pelo servidor
    
    public static final float WIDTH = 32;
    public static final float HEIGHT = 48;
    
    private boolean facingRight = false; // Controlado pelo servidor

    // Construtor padrão (usado para criar "fantasmas" de inimigos)
    public Enemy(float x, float y) {
        super(new Vector2(x, y), new Vector2(WIDTH, HEIGHT), "inimigo_Fase1migo");
    }

    // UPDATE É APENAS PARA ANIMAÇÃO
    @Override // Sobrescreve o update do GameObject
    public void update(float delta) { // Renomeado de volta para update
        // Avança o frame de animação
        super.update(delta);
    }
    
    // --- MÉTODOS DE REDE ADICIONADOS ---
    public void setPosition(float x, float y) {
        if (this.position == null) this.position = new Vector2();
        this.position.set(x, y);
    }
    
    public void setFacing(boolean facingRight) {
        this.facingRight = facingRight;
    }
    // ---------------------------------
    
    // Lógica de "vida" não existe mais no cliente
    // public void takeDamage(int dmg) { }
    // public boolean isAlive() { return true; } // O servidor decide se está vivo

    @Override
    public void draw(SpriteBatch batch) {
        if (currentFrame == null) return;
        
        // Vira o sprite
        if (!facingRight && !currentFrame.isFlipX())
            currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX())
            currentFrame.flip(true, false);
            
        batch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
    }

    // Métodos estáticos não são mais usados pelo cliente
    public static void updateAll(float delta) { }
    public static void drawAll(SpriteBatch batch) { }
    public static void disposeAll() { }
    public static void createEnemies(int level) { }
}