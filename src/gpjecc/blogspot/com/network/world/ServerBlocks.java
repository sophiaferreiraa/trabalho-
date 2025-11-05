package gpjecc.blogspot.com.network.world;

import java.util.ArrayList;
import java.util.List;

public class ServerBlocks {
    private List<ServerRectangle> blocks;

    public ServerBlocks(int level) {
        blocks = new ArrayList<>(); // Inicializa a lista
        
        if (level == 1) {
            // Layout Nível 1 (COPIADO EXATAMENTE DO SEU Blocks.java)
            blocks.add(new ServerRectangle(0, 0, 2048, 50));
            blocks.add(new ServerRectangle(0, 214, 2048, 20));
            blocks.add(new ServerRectangle(0, 0, 30, 222));
            blocks.add(new ServerRectangle(20, 125, 85, 5));
            blocks.add(new ServerRectangle(158, 125, 42, 5));
            blocks.add(new ServerRectangle(222, 125, 40, 5));
            blocks.add(new ServerRectangle(285, 125, 42, 5));
            blocks.add(new ServerRectangle(350, 125, 45, 5));
            blocks.add(new ServerRectangle(460, 125, 95, 5));
            blocks.add(new ServerRectangle(268, 117, 16, 105));
            blocks.add(new ServerRectangle(460, 127, 100, 5));
            blocks.add(new ServerRectangle(492, 48, 16, 80));
            blocks.add(new ServerRectangle(778, 127, 30, 5));
            blocks.add(new ServerRectangle(832, 127, 42, 5));
            blocks.add(new ServerRectangle(924, 127, 176, 5));
            blocks.add(new ServerRectangle(1002, 48, 16, 80));
            blocks.add(new ServerRectangle(1147, 127, 113, 5));
            blocks.add(new ServerRectangle(1389, 0, 16, 48));
            blocks.add(new ServerRectangle(1468, 118, 16, 95));
            blocks.add(new ServerRectangle(1626, 127, 32, 5));
            blocks.add(new ServerRectangle(1389, 64, 16, 48));
            blocks.add(new ServerRectangle(1755, 118, 16, 95));
            blocks.add(new ServerRectangle(1680, 127, 50, 5));
            blocks.add(new ServerRectangle(1915, 133, 16, 70));
            blocks.add(new ServerRectangle(1915, 133, 65, 15));
            blocks.add(new ServerRectangle(2032, 0, 16, 222)); 
        } 
        // (Lógica do Nível 2 omitida por enquanto)
    }
    
    public List<ServerRectangle> getBlocks() {
        return blocks;
    }
}