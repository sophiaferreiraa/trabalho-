package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;
import java.util.UUID;

public class PlayerState implements Serializable {
    public UUID playerId;
    public String name;
    public float x, y;
    public float vx, vy;
    public int hp;
    public boolean facingRight;

    private static final long serialVersionUID = 1L;
}
