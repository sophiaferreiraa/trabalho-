package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import gpjecc.blogspot.com.GameObjects.Blocks.Blocks;
import gpjecc.blogspot.com.GameObjects.Blocks.Ladders;
import gpjecc.blogspot.com.GameObjects.Blocks.LadderBlock;
import gpjecc.blogspot.com.GameObjects.Blocks.TileBlock;
import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.GameObjects.Base.GameObject;

import gpjecc.blogspot.com.Observers.PlayerObserver;
import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject {
    private boolean spacePressedLastFrame = false;
    private boolean zPressedLastFrame = false;
    private boolean pageUpPressedLastFrame = false;

    private static final float GRAVITY = -500f;
    private static final float JUMP_VELOCITY = 250f;
    private static final float MOVE_SPEED = 150f;
    private static final float DOWN_SPEED = 100f;
    private static final float CLIMB_SPEED = 100f;

    private float velocityY;
    private boolean onGround;
    private boolean facingRight;

    private boolean isAttacking;
    private float attackTimer;
    private static final float ATTACK_DURATION = 0.3f;

    private float health; 
    private float damageCooldown = 1f;
    private float damageTimer = 0f;

    private int score = 0;

    private boolean isClimbing = false;
    // private float climbTargetY; // <-- VARIÁVEL REMOVIDA (não estava sendo usada)

    private boolean isRemote = false; 
    private String name = ""; 

    private List<PlayerObserver> observers = new ArrayList<>();

    private Blocks blocks;
    private Ladders ladders;

    // Construtor principal (Jogador Local)
    public Player() {
        super(new Vector2(100, 100), new Vector2(23, 45), "spriteTartaruga");

        this.isRemote = false; 
        this.animationFrames = AssetsManager.getInstance().getFrames("spriteTartaruga", new Vector2(32, 64));
        this.animation = AssetsManager.getInstance().getAnimation(animationFrames, 0, 0.15f);
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);

        velocityY = 0;
        onGround = false;
        facingRight = true;
        isAttacking = false;
        attackTimer = 0;
        this.health = 5f; 
    }

    // Construtor "leve" (Jogador Remoto / Fantasma)
    protected Player(boolean noLoad) {
        super(new Vector2(0, 0), new Vector2(23, 45), "spriteTartaruga");
        this.isRemote = true; 
        
        this.animationFrames = AssetsManager.getInstance().getFrames("spriteTartaruga", new Vector2(32, 64));
        this.animation = AssetsManager.getInstance().getAnimation(animationFrames, 0, 0.15f);
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);

        this.velocityY = 0;
        this.onGround = false;
        this.facingRight = true;
        this.isAttacking = false;
        this.attackTimer = 0;
        this.health = 5f; 
    }

    public void setLevelDependencies(Blocks blocks, Ladders ladders) {
        this.blocks = blocks;
        this.ladders = ladders;
    }

    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

    // --- Notificadores do Observer ---
    private void notifyDamageTaken() {
        for (PlayerObserver observer : observers) {
            observer.onPlayerDamaged(health);
        }
    }

    private void notifyAttack() {
        for (PlayerObserver observer : observers) {
            observer.onPlayerAttack();
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

    public Vector2 getPosition() {
        return position;
    }

    public float getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int value) {
        score += value;
        notifyScoreChanged(); 
    }

    // Dano local (Inimigos)
    public void tryTakeDamage(float damage) {
        if (damageTimer <= 0 && isAlive()) {
            health -= damage;
            if (health < 0)
                health = 0;
            damageTimer = damageCooldown;
            notifyDamageTaken(); 
            if (health <= 0) {
                notifyPlayerDied(); 
            }
        }
    }

    public void update(float delta) {
        // --- LÓGICA DE FANTASMA REMOTO ---
        if (isRemote) { 
            if (isAttacking) {
                attackTimer += delta;
                if (attackTimer >= ATTACK_DURATION) {
                    isAttacking = false;
                }
            }
            if (currentFrame == null) return; 
            if (!facingRight && !currentFrame.isFlipX())
                currentFrame.flip(true, false);
            if (facingRight && currentFrame.isFlipX())
                currentFrame.flip(true, false);
            return;
        }
        // --- FIM DA LÓGICA REMOTA ---

        // O código abaixo só roda para o JOGADOR LOCAL
        if (damageTimer > 0)
            damageTimer -= delta;

        float moveX = 0;
        Rectangle playerRect = new Rectangle(position.x, position.y, size.x, size.y);

        // Lógica de Escada
        Array<LadderBlock> laddersArray = this.ladders.getLadders();
        for (LadderBlock ladderBlock : laddersArray) { 
            Rectangle ladderRect = ladderBlock.getBounds();
            if (playerRect.overlaps(ladderRect)) {
                if (Gdx.input.isKeyPressed(Keys.UP) && !pageUpPressedLastFrame && !isClimbing) {
                    isClimbing = true;
                    // climbTargetY foi removido
                    position.x = ladderRect.x + (ladderRect.width / 2) - (size.x / 2);
                    velocityY = 0;
                }
                if (isClimbing) {
                    float playerBottom = position.y;
                    if (Gdx.input.isKeyPressed(Keys.UP)) {
                        position.y += CLIMB_SPEED * delta;
                    } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                        if (playerBottom > ladderRect.y + 1f) { 
                            position.y -= CLIMB_SPEED * delta;
                        } else {
                            isClimbing = false;
                            velocityY = GRAVITY * delta;
                        }
                    }
                    velocityY = 0;
                    onGround = false;
                    if (position.y >= ladderBlock.getTopY()) {
                        isClimbing = false;
                        onGround = true;
                        velocityY = GRAVITY * delta;
                    }
                    return; 
                }
            } else if (isClimbing) {
                isClimbing = false;
                velocityY = GRAVITY * delta;
            }
        }

        // Movimento horizontal
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX = -MOVE_SPEED;
            facingRight = false;
            super.update(delta); 
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX = MOVE_SPEED;
            facingRight = true;
            super.update(delta); 
        } else {
            currentFrame = animationFrames[0][0];
        }
        position.x += moveX * delta;

        // Agachar
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            position.y -= DOWN_SPEED * delta;
        }

        // Pulo
        if (Gdx.input.isKeyPressed(Keys.SPACE) && onGround && !spacePressedLastFrame) {
            velocityY = JUMP_VELOCITY;
            onGround = false;
        }

        // Gravidade
        if (!onGround) {
            velocityY += GRAVITY * delta;
        }
        position.y += velocityY * delta;

        // Ataque (Lógica local para animação e inimigos)
        if (Gdx.input.isKeyPressed(Keys.Z) && !isAttacking && !zPressedLastFrame) {
            isAttacking = true;
            attackTimer = 0;
            notifyAttack(); 
            Gdx.app.log("Player", "Atacou!");
        }

        if (isAttacking) {
            attackTimer += delta;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
            }
        }

        if (!facingRight && !currentFrame.isFlipX())
            currentFrame.flip(true, false);
        if (facingRight && currentFrame.isFlipX())
            currentFrame.flip(true, false);

        spacePressedLastFrame = Gdx.input.isKeyPressed(Keys.SPACE);
        zPressedLastFrame = Gdx.input.isKeyPressed(Keys.Z);
        pageUpPressedLastFrame = Gdx.input.isKeyPressed(Keys.UP);

        checkCollision();
    }

    public void checkCollision() {
        Rectangle playerRect = new Rectangle(position.x, position.y, size.x, size.y);
        Array<TileBlock> blocksArray = this.blocks.getBlocks();
        onGround = false;

        for (TileBlock block : blocksArray) { 
            Rectangle blockRect = block.getBounds();
            if (playerRect.overlaps(blockRect)) {
                float playerBottom = position.y;
                float playerTop = position.y + size.y;
                float playerLeft = position.x;
                float playerRight = position.x + size.x;
                float blockBottom = blockRect.y;
                float blockTop = blockRect.y + blockRect.height;
                float blockLeft = blockRect.x;
                float blockRight = blockRect.x + blockRect.width;
                if (velocityY <= 0 && playerBottom < blockTop && playerTop > blockTop && playerRight > blockLeft && playerLeft < blockRight) {
                    position.y = blockTop;
                    velocityY = 0;
                    onGround = true;
                    playerRect.y = position.y;
                    continue;
                }
                if (velocityY > 0 && playerTop > blockBottom && playerBottom < blockBottom) {
                    position.y = blockBottom - size.y;
                    velocityY = 0;
                    playerRect.y = position.y;
                    continue;
                }
                if (playerRight > blockLeft && playerLeft < blockLeft && playerTop > blockBottom && playerBottom < blockTop && facingRight) {
                    position.x = blockLeft - size.x;
                    playerRect.x = position.x;
                } else if (playerLeft < blockRight && playerRight > blockRight && playerTop > blockBottom && playerBottom < blockTop && !facingRight) {
                    position.x = blockRight;
                    playerRect.x = position.x;
                }
            }
        }
    }

    // --- MÉTODOS DE REDE ---
    public static Player createNetworkGhost(float x, float y, Blocks blocks, Ladders ladders) {
        Player p = new Player(true); 
        p.position.set(x, y);
        p.setLevelDependencies(blocks, ladders);
        p.isRemote = true;
        return p;
    }

    public void performNetworkAttack() {
        if (isRemote && !isAttacking) {
            this.isAttacking = true;
            this.attackTimer = 0;
        }
    }
    // ------------------------------------

    public boolean isAttacking() {
        return isAttacking;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, size.x, size.y);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(float x, float y) {
        if (this.position == null)
            this.position = new com.badlogic.gdx.math.Vector2();
        this.position.x = x;
        this.position.y = y;
    }

    public void setHp(float hp) {
        float oldHealth = this.health;
        this.health = hp;
        
        if (this.health < oldHealth && !isRemote) {
            notifyDamageTaken(); 
        }
        if (this.health <= 0 && oldHealth > 0 && !isRemote) {
            notifyPlayerDied(); 
        }
    }

    public void setFacing(boolean facingRight) {
        this.facingRight = facingRight;
    }
}