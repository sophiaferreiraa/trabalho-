package gpjecc.blogspot.com.network.world;

import java.util.ArrayList;
import java.util.List;

public class ServerLadders {
    private List<ServerRectangle> ladders;

    public ServerLadders(int level) {
        ladders = new ArrayList<>();
        
        float groundY = 50;
        float platformY = 125;
        float ladderHeight = platformY - groundY; 

        if (level == 1) {
            ladders.add(new ServerRectangle(204, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(330, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(814, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(1663, groundY, 15, ladderHeight));
        }
        else if (level == 2) {
            // Layout Nível 2 (COPIADO EXATAMENTE DO SEU Ladders.java)
            ladders.add(new ServerRectangle(260, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(460, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(660, groundY, 15, ladderHeight));
            ladders.add(new ServerRectangle(860, groundY, 15, ladderHeight));
        }
    }
    
    public List<ServerRectangle> getLadders() {
        return ladders;
    }
}