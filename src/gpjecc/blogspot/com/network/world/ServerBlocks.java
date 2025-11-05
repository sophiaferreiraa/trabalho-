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
        else if (level == 2) {
            // Layout Nível 2 (COPIADO EXATAMENTE DO SEU Blocks.java)
            blocks.add(new ServerRectangle(0, 0, 2048, 36));
            blocks.add(new ServerRectangle(0, 240, 2048, 16));
            blocks.add(new ServerRectangle(0, 0, 15, 256));
            blocks.add(new ServerRectangle(2048, 0, 16, 222));
            blocks.add(new ServerRectangle(0, 146, 450, 5));
            blocks.add(new ServerRectangle(477, 146, 488, 5));
            blocks.add(new ServerRectangle(988, 146, 486, 5));
            blocks.add(new ServerRectangle(1498, 146, 490, 5));
            blocks.add(new ServerRectangle(79, 36, 200, 21));
            blocks.add(new ServerRectangle(592, 36, 200, 21));
            blocks.add(new ServerRectangle(1105, 36, 200, 21));
            blocks.add(new ServerRectangle(1615, 36, 200, 21));
            blocks.add(new ServerRectangle(309, 36, 27, 42));
            blocks.add(new ServerRectangle(399, 36, 27, 42));
            blocks.add(new ServerRectangle(823, 36, 27, 42));
            blocks.add(new ServerRectangle(910, 36, 27, 42));
            blocks.add(new ServerRectangle(1336, 36, 27, 42));
            blocks.add(new ServerRectangle(1422, 36, 27, 42));
            blocks.add(new ServerRectangle(1846, 36, 27, 42));
            blocks.add(new ServerRectangle(1933, 36, 27, 42));
            blocks.add(new ServerRectangle(396, 148, 27, 42));
            blocks.add(new ServerRectangle(534, 148, 27, 42));
            blocks.add(new ServerRectangle(912, 148, 27, 42));
            blocks.add(new ServerRectangle(1045, 148, 27, 42));
            blocks.add(new ServerRectangle(1422, 148, 27, 42));
            blocks.add(new ServerRectangle(1933, 148, 27, 42));
            blocks.add(new ServerRectangle(1558, 148, 27, 42));
            blocks.add(new ServerRectangle(109, 148, 174, 42));
            blocks.add(new ServerRectangle(612, 148, 174, 42));
            blocks.add(new ServerRectangle(1134, 148, 174, 42));
            blocks.add(new ServerRectangle(1644, 148, 174, 42));
            blocks.add(new ServerRectangle(81, 148, 27, 21));
            blocks.add(new ServerRectangle(312, 148, 27, 21));
            blocks.add(new ServerRectangle(592, 148, 27, 21));
            blocks.add(new ServerRectangle(823, 148, 27, 21));
            blocks.add(new ServerRectangle(1101, 148, 27, 21));
            blocks.add(new ServerRectangle(1335, 148, 27, 21));
            blocks.add(new ServerRectangle(1615, 148, 27, 21));
            blocks.add(new ServerRectangle(1846, 148, 27, 21));
            blocks.add(new ServerRectangle(339, 36, 27, 21));
            blocks.add(new ServerRectangle(850, 36, 27, 21));
            blocks.add(new ServerRectangle(1363, 36, 27, 21));
            blocks.add(new ServerRectangle(1875, 36, 27, 21));
        }
    }
    
    public List<ServerRectangle> getBlocks() {
        return blocks;
    }
}