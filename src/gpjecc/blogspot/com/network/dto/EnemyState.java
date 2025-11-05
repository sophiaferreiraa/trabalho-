package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;
import java.util.UUID;

public class EnemyState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public UUID id;
    public float x, y;
    public int health;
    public boolean facingRight;
    
    // --- CAMPOS PARA IA DE PATRULHA ---
    public float patrolStart;
    public float patrolEnd;
    public int direction; // 1 = direita, -1 = esquerda
    // ---------------------------------
    
    public EnemyState() {}
    
    public EnemyState(UUID id, float x, float y, int health, float patrolStart, float patrolEnd) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.health = health;
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
        this.direction = 1; // Começa indo para a direita
        this.facingRight = true;
    }
}