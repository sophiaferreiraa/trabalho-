package gpjecc.blogspot.com.network.dto;


import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class GameSnapshot implements Serializable {
    public UUID id;
    public Map<UUID, PlayerState> players;
    public long serverTimestamp;
}
