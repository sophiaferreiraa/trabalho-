package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class LobbyState implements Serializable {
    private static final long serialVersionUID = 1L;

    // "LOBBY" ou "IN_GAME"
    public String gameState; 
    
    // Lista de jogadores no lobby
    public Map<UUID, String> playerNames; 
    
    // ID do jogador que é o host
    public UUID hostId;
}
