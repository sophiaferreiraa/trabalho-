package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Blocks {
    // private static Blocks INSTANCE = null; // REMOVIDO
    private Array<TileBlock> blocks;

    // Construtor agora é público e recebe o nível
    public Blocks(int level) {
        blocks = new Array<TileBlock>();

        if (level == 1) {
            // Layout Nível 1 (copiado do seu código original)
            // Usando "red.png" (definido em TileBlock)
            blocks.add(new TileBlock(0, 0, 2048, 50, "red"));
            blocks.add(new TileBlock(0, 214, 2048, 20, "red"));
            blocks.add(new TileBlock(0, 0, 30, 222, "red"));
            blocks.add(new TileBlock(20, 125, 85, 5, "red"));
            blocks.add(new TileBlock(158, 125, 42, 5, "red"));
            blocks.add(new TileBlock(222, 125, 40, 5, "red"));
            blocks.add(new TileBlock(285, 125, 42, 5, "red"));
            blocks.add(new TileBlock(350, 125, 45, 5, "red"));
            blocks.add(new TileBlock(460, 125, 95, 5, "red"));
            blocks.add(new TileBlock(268, 117, 16, 105, "red"));
            blocks.add(new TileBlock(460, 127, 100, 5, "red"));
            blocks.add(new TileBlock(492, 48, 16, 80, "red"));
            blocks.add(new TileBlock(778, 127, 30, 5, "red"));
            blocks.add(new TileBlock(832, 127, 42, 5, "red"));
            blocks.add(new TileBlock(924, 127, 176, 5, "red"));
            blocks.add(new TileBlock(1002, 48, 16, 80, "red"));
            blocks.add(new TileBlock(1147, 127, 113, 5, "red"));
            blocks.add(new TileBlock(1389, 0, 16, 48, "red"));
            blocks.add(new TileBlock(1468, 118, 16, 95, "red"));
            blocks.add(new TileBlock(1626, 127, 32, 5, "red"));
            blocks.add(new TileBlock(1389, 64, 16, 48, "red"));
            blocks.add(new TileBlock(1755, 118, 16, 95, "red"));
            blocks.add(new TileBlock(1680, 127, 50, 5, "red"));
            blocks.add(new TileBlock(1915, 133, 16, 70, "red"));
            blocks.add(new TileBlock(1915, 133, 65, 15, "red"));
            blocks.add(new TileBlock(2032, 0, 16, 222, "red")); // Parede direita

        } else if (level == 2) {
            // Layout Nível 2 (Exemplo simples)
            // Usando "blue.png"
            // Chão e Teto
            blocks.add(new TileBlock(0, 0, 2048, 36, "blue"));
            blocks.add(new TileBlock(0, 240, 2048, 16, "blue"));
            // Paredes
            blocks.add(new TileBlock(0, 0, 15, 256, "blue"));
            blocks.add(new TileBlock(2048, 0, 16, 222, "blue")); // Parede final
            // Plataformas
            blocks.add(new TileBlock(0, 146, 450, 5, "blue"));
            blocks.add(new TileBlock(477, 146, 488, 5, "blue"));
            blocks.add(new TileBlock(988, 146, 486, 5, "blue"));
            blocks.add(new TileBlock(1498, 146, 490, 5, "blue"));

            //caixas largas
            blocks.add(new TileBlock(79, 36, 200, 21, "blue"));
            blocks.add(new TileBlock(592, 36, 200, 21, "blue"));
            blocks.add(new TileBlock(1105, 36, 200, 21, "blue"));
            blocks.add(new TileBlock(1615, 36, 200, 21, "blue"));

            //caixas longas
            blocks.add(new TileBlock(309, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(399, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(823, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(910, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(1336, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(1422, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(1846, 36, 27, 42, "blue"));
            blocks.add(new TileBlock(1933, 36, 27, 42, "blue"));

            blocks.add(new TileBlock(396, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(534, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(912, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(1045, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(1422, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(1933, 148, 27, 42, "blue"));
            blocks.add(new TileBlock(1558, 148, 27, 42, "blue"));

            //caixas largas superior
            blocks.add(new TileBlock(109, 148, 174, 42, "blue"));
            blocks.add(new TileBlock(612, 148, 174, 42, "blue"));
            blocks.add(new TileBlock(1134, 148, 174, 42, "blue"));
            blocks.add(new TileBlock(1644, 148, 174, 42, "blue"));

            //caxinhas
            blocks.add(new TileBlock(81, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(312, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(592, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(823, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(1101, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(1335, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(1615, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(1846, 148, 27, 21, "blue"));
            blocks.add(new TileBlock(339, 36, 27, 21, "blue"));
            blocks.add(new TileBlock(850, 36, 27, 21, "blue"));
            blocks.add(new TileBlock(1363, 36, 27, 21, "blue"));
            blocks.add(new TileBlock(1875, 36, 27, 21, "blue"));

        }
    }

    public Array<TileBlock> getBlocks() {
        return blocks;
    }

    public void draw(SpriteBatch batch) {
        for (TileBlock block : blocks) {
            block.draw(batch);
        }
    }
}