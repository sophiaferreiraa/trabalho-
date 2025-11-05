package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;
import java.util.UUID;

public class PlayerState implements Serializable {
    public UUID playerId;
    public String name;
    public float x, y;
    public float vx, vy;
    public float hp; 
    public boolean facingRight;
    
    // --- CAMPOS ADICIONADOS ---
    public int score;
    public boolean onGround;
    public boolean isClimbing;
    public boolean isDead;
    // ------------------------

    public boolean isAttacking;
    public float attackCooldown;
    public float damageCooldown; 

    private static final long serialVersionUID = 1L;
}