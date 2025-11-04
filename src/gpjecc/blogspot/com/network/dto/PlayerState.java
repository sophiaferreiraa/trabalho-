package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;
import java.util.UUID;

public class PlayerState implements Serializable {
    public UUID playerId;
    public String name;
    public float x, y;
    public float vx, vy;
    public float hp; // MODIFICADO de int para float
    public boolean facingRight;

    // --- CAMPOS ADICIONADOS (VERIFIQUE SE ELES ESTÃO AQUI) ---
    public boolean isAttacking;
    public float attackCooldown;
    public float damageCooldown; 
    // ---------------------------------------------------------

    private static final long serialVersionUID = 1L;
}