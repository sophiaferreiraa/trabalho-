package gpjecc.blogspot.com.network.dto;


import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class GameSnapshot implements Serializable {
    public UUID id;
    public long serverTimestamp;
    
    // Mapas de estado
    public Map<UUID, PlayerState> players;
    
    // --- CAMPO ADICIONADO ---
    public Map<UUID, EnemyState> enemies;
    // ------------------------
}