package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Importado
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import gpjecc.blogspot.com.GameObjects.Blocks.Blocks;
import gpjecc.blogspot.com.GameObjects.Blocks.Ladders;
// import gpjecc.blogspot.com.GameObjects.Blocks.LadderBlock; // Não mais usado
// import gpjecc.blogspot.com.GameObjects.Blocks.TileBlock; // Não mais usado
import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.GameObjects.Base.GameObject;

import gpjecc.blogspot.com.Observers.PlayerObserver;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    
    // --- Variáveis de ESTADO (controladas pelo servidor) ---
    private float velocityY = 0; // Usado apenas para animação
    private boolean onGround = true;
    private boolean facingRight = true;
    private boolean isAttacking = false;
    private float health = 5f; 
    private int score = 0;
    private boolean isClimbing = false;
    private boolean isDead = false;
    // ---------------------------------------------------

    private float attackTimer;
    private static final float ATTACK_DURATION = 0.3f;

    private boolean isRemote = false; 
    private String name = ""; 

    private List<PlayerObserver> observers = new ArrayList<>();

    // Construtor principal (Jogador Local)
    public Player() {
        super(new Vector2(100, 100), new Vector2(23, 45), "spriteTartaruga");
        init();
        this.isRemote = false; 
    }

    // Construtor "leve" (Jogador Remoto / Fantasma)
    protected Player(boolean noLoad) {
        super(new Vector2(0, 0), new Vector2(23, 45), "spriteTartaruga");
        init();
        this.isRemote = true; 
    }
    
    private void init() {
        this.animationFrames = AssetsManager.getInstance().getFrames("spriteTartaruga", new Vector2(32, 64));
        this.animation = AssetsManager.getInstance().getAnimation(animationFrames, 0, 0.15f);
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);
        this.health = 5f; 
        this.score = 0;
    }

    // Não precisamos mais disso, o servidor cuida de tudo
    public void setLevelDependencies(Blocks blocks, Ladders ladders) { }

    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    // --- Notificadores do Observer ---
    private void notifyDamageTaken() {
        for (PlayerObserver observer : observers) {
            observer.onPlayerDamaged(health);
        }
    }
    private void notifyScoreChanged() {
        for (PlayerObserver observer : observers) {
            observer.onScoreChanged(score);
        }
    }
    private void notifyPlayerDied() {
        for (PlayerObserver observer : observers) {
            observer.onPlayerDied();
        }
    }
    // ---------------------------------
    
    public float getHealth() { return health; }
    public int getScore() { return score; }
    public boolean isAlive() { return !isDead; }

    /**
     * Este método agora APENAS atualiza as animações,
     * baseado no estado que o servidor enviou.
     */
    @Override // Sobrescreve o update do GameObject
    public void update(float delta) { // Renomeado de volta para update
        if (isDead) {
            // TODO: Adicionar animação de morte
            currentFrame = animationFrames[0][0]; // Frame padrão por enquanto
            return;
        }

        // Lógica de animação de ataque
        if (isAttacking) {
            attackTimer += delta;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
            }
            
            // --- CORREÇÃO DO CRASH ---
            // Vamos usar o frame 2 (índice 2) como ataque, pois sabemos que ele existe (0-3)
            currentFrame = animationFrames[0][2]; 
            // -------------------------
            
        } 
        // Lógica de animação de escada
        else if (isClimbing) {
            // TODO: Adicionar frame de escada
            currentFrame = animationFrames[0][0]; // Frame padrão por enquanto
        }
        // Lógica de animação de pulo/queda
        else if (!onGround) {
            currentFrame = animationFrames[0][3]; // Supõe que o 4º frame é de pulo
        }
        // Lógica de animação de movimento
        else if (Math.abs(velocityY) > 0.1f) { // Se movendo
            super.update(delta); // Avança a animação de andar (chama o update da superclasse)
        }
        // Lógica de animação de parado
        else {
            currentFrame = animationFrames[0][0]; // Parado
        }

        if (currentFrame == null) return; 
        
        // Vira o sprite (sempre baseado no estado)
        if (!facingRight && !currentFrame.isFlipX())
            currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX())
            currentFrame.flip(true, false);
            
        // --- NENHUMA FÍSICA AQUI ---
    }
    
    // --- checkLocalCollision() FOI REMOVIDO ---

    // --- MÉTODOS DE REDE (Chamados por TelaNivel1) ---
    public static Player createNetworkGhost(float x, float y) {
        Player p = new Player(true); 
        p.position.set(x, y);
        p.isRemote = true;
        return p;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, size.x, size.y);
    }

    public void setName(String name) { this.name = name; }
    public void setPosition(float x, float y) { this.position.set(x, y); }
    public void setVelocity(float vx, float vy) { this.velocityY = vy; }
    public void setOnGround(boolean onGround) { this.onGround = onGround; }
    public void setClimbing(boolean isClimbing) { this.isClimbing = isClimbing; }
    public void setFacing(boolean facingRight) { this.facingRight = facingRight; }
    
    public void setAttacking(boolean isAttacking) {
        if (isAttacking && !this.isAttacking) {
             this.isAttacking = true;
             this.attackTimer = 0;
        }
    }

    public void setHp(float hp) {
        float oldHealth = this.health;
        this.health = hp;
        
        if (this.health < oldHealth && !isRemote) {
            notifyDamageTaken(); 
        }
    }
    
    public void setScore(int score) {
        int oldScore = this.score;
        this.score = score;
        if (this.score > oldScore && !isRemote) {
            notifyScoreChanged();
        }
    }
    
    public void setDead(boolean isDead) {
        boolean wasDead = this.isDead;
        this.isDead = isDead;
        if (this.isDead && !wasDead && !isRemote) {
            notifyPlayerDied();
        }
    }
}