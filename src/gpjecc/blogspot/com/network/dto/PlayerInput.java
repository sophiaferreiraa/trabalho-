package gpjecc.blogspot.com.network.dto;

import java.io.Serializable;

public class PlayerInput implements Serializable {
    public long clientTimestamp;
    public boolean left;
    public boolean right;
    public boolean jump;
    public boolean attack;
    
    // --- CAMPOS ADICIONADOS ---
    public boolean up;
    public boolean down;
    // --------------------------

    public int sequenceNumber;
}