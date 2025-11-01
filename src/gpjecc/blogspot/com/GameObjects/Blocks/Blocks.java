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
            blocks.add(new TileBlock(0, 0, 1024, 50, "blue"));
            blocks.add(new TileBlock(0, 214, 1024, 20, "blue"));
            // Paredes
            blocks.add(new TileBlock(0, 0, 30, 222, "blue"));
            blocks.add(new TileBlock(1008, 0, 16, 222, "blue")); // Parede final
            // Plataformas
            blocks.add(new TileBlock(100, 125, 150, 5, "blue"));
            blocks.add(new TileBlock(300, 125, 150, 5, "blue"));
            blocks.add(new TileBlock(500, 125, 150, 5, "blue"));
            blocks.add(new TileBlock(700, 125, 150, 5, "blue"));
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

    /* REMOVIDO
    public static Blocks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blocks();
        }
        return INSTANCE;
    }
    */
}