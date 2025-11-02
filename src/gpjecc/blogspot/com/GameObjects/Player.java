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

    private float health = 5f;
    private float damageCooldown = 1f;
    private float damageTimer = 0f;

    private int score = 0;

    private boolean isClimbing = false;
    private float climbTargetY;

    // Lista de observadores
    private List<PlayerObserver> observers = new ArrayList<>();

    // --- MUDANÇA ---
    // Dependências do Nível (NÃO SÃO MAIS ESTÁTICAS)
    private Blocks blocks;
    private Ladders ladders;
    // --- FIM DA MUDANÇA ---

    // --- MUDANÇA ---
    // Construtor agora recebe as dependências do nível
    public Player(Blocks blocks, Ladders ladders) {
        super(new Vector2(100, 100), new Vector2(23, 45), "spriteTartaruga");

        this.blocks = blocks; // Armazena os blocos do nível
        this.ladders = ladders; // Armazena as escadas do nível

        this.animationFrames = AssetsManager.getInstance().getFrames("spriteTartaruga", new Vector2(32, 64));
        this.animation = AssetsManager.getInstance().getAnimation(animationFrames, 0, 0.15f);
        this.currentFrame = AssetsManager.getInstance().getCurrentFrame(animation);

        velocityY = 0;
        onGround = false;
        facingRight = true;
        isAttacking = false;
        attackTimer = 0;
    }
    // --- FIM DA MUDANÇA ---

    // Métodos para gerenciar observadores
    public void addObserver(PlayerObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(PlayerObserver observer) {
        observers.remove(observer);
    }

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
        notifyScoreChanged(); // Notifica observadores
    }

    public void tryTakeDamage(float damage) {
        if (damageTimer <= 0 && isAlive()) {
            health -= damage;
            if (health < 0)
                health = 0;
            damageTimer = damageCooldown;
            notifyDamageTaken(); // Notifica observadores
            if (health <= 0) {
                notifyPlayerDied(); // Notifica morte
            }
        }
    }

    public void update(float delta) {
        if (damageTimer > 0)
            damageTimer -= delta;

        float moveX = 0;

        // Verifica escada
        Rectangle playerRect = new Rectangle(position.x, position.y, size.x, size.y);
        
        // --- MUDANÇA ---
        // USA A INSTÂNCIA DO NÍVEL, NÃO O SINGLETON
        Array<LadderBlock> laddersArray = this.ladders.getLadders();
        // --- FIM DA MUDANÇA ---

        // Colisão com a escada (entrada e permanência)
        for (LadderBlock ladderBlock : laddersArray) { // <--- MUDANÇA
            Rectangle ladderRect = ladderBlock.getBounds();

            if (playerRect.overlaps(ladderRect)) {
                // Se pressionou UP e ainda não está subindo
                if (Gdx.input.isKeyPressed(Keys.UP) && !pageUpPressedLastFrame && !isClimbing) {
                    isClimbing = true;
                    climbTargetY = ladderBlock.getTopY();

                    // Centraliza o jogador na escada
                    position.x = ladderRect.x + (ladderRect.width / 2) - (size.x / 2);
                    velocityY = 0;
                }

                if (isClimbing) {
                    float playerBottom = position.y;

                    if (Gdx.input.isKeyPressed(Keys.UP)) {
                        position.y += CLIMB_SPEED * delta;
                    } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                        // Só desce se ainda está dentro da escada
                        if (playerBottom > ladderRect.y + 1f) { // 1f de margem
                            position.y -= CLIMB_SPEED * delta;
                        } else {
                            // Saiu da base da escada
                            isClimbing = false;
                            velocityY = GRAVITY * delta;
                        }
                    }

                    velocityY = 0;
                    onGround = false;

                    // Saiu por cima
                    if (position.y >= ladderBlock.getTopY()) {
                        isClimbing = false;
                        onGround = true;
                        velocityY = GRAVITY * delta;
                    }

                    return; // Ignora colisão com blocos neste frame
                }
            } else if (isClimbing) {
                // Se não está mais sobre a escada
                isClimbing = false;
                velocityY = GRAVITY * delta;
            }
        }

        // Movimento horizontal
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            moveX = -MOVE_SPEED;
            facingRight = false;
            super.update(delta); // <-- CORREÇÃO AQUI
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            moveX = MOVE_SPEED;
            facingRight = true;
            super.update(delta); // <-- CORREÇÃO AQUI
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

        // Ataque
        if (Gdx.input.isKeyPressed(Keys.Z) && !isAttacking && !zPressedLastFrame) {
            isAttacking = true;
            attackTimer = 0;
            notifyAttack(); // Notifica observadores
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

    /*@Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);

        for (LadderBlock ladder : Ladders.getInstance().getLadders()) {
            ladder.draw(batch);
        }
    }*/

    public void checkCollision() {
        Rectangle playerRect = new Rectangle(position.x, position.y, size.x, size.y);
        
        // --- MUDANÇA ---
        // USA A INSTÂNCIA DO NÍVEL, NÃO O SINGLETON
        Array<TileBlock> blocksArray = this.blocks.getBlocks();
        // --- FIM DA MUDANÇA ---
        
        onGround = false;

        for (TileBlock block : blocksArray) { // <--- MUDANÇA
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

                if (velocityY <= 0 && playerBottom < blockTop && playerTop > blockTop && playerRight > blockLeft
                        && playerLeft < blockRight) {
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

                if (playerRight > blockLeft && playerLeft < blockLeft && playerTop > blockBottom
                        && playerBottom < blockTop && facingRight) {
                    position.x = blockLeft - size.x;
                    playerRect.x = position.x;
                } else if (playerLeft < blockRight && playerRight > blockRight && playerTop > blockBottom
                        && playerBottom < blockTop && !facingRight) {
                    position.x = blockRight;
                    playerRect.x = position.x;
                }
            }
        }
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    // --- MUDANÇA ---
    // Adiciona este método para facilitar a verificação de colisão na tela
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, size.x, size.y);
    }
    // --- FIM DA MUDANÇA ---
}