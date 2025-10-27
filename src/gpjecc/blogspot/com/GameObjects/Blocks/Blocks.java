package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class Blocks {
    private static Blocks INSTANCE = null;
    private Array<TileBlock> blocks;

    private Blocks() {
        blocks = new Array<TileBlock>();

        // Chão principal - grande bloco na base
        blocks.add(new TileBlock(0, 0, 2048, 50));

        // teto principal - grande bloco na base
        blocks.add(new TileBlock(0, 214, 2048, 20));
        blocks.add(new TileBlock(0, 0, 30, 222));       // Parede esquerda
        blocks.add(new TileBlock(20, 125, 85, 5));      // Plataforma 1
        blocks.add(new TileBlock(158, 125, 42, 5));     // Plataforma 2
        blocks.add(new TileBlock(222, 125, 40, 5));     // Plataforma 3 (y e height ajustados)
        blocks.add(new TileBlock(285, 125, 42, 5));     // Plataforma 4
        blocks.add(new TileBlock(350, 125, 45, 5));     // Plataforma 5
        blocks.add(new TileBlock(460, 125, 95, 5));    
        // Plataformas e paredes (todos os blocos visíveis)
        blocks.add(new TileBlock(0, 0, 30, 222));       // Parede esquerda
        blocks.add(new TileBlock(268, 117, 16, 105));     // Pilar
        blocks.add(new TileBlock(460, 127, 100, 5));     // Plataforma 5**
        blocks.add(new TileBlock(492, 48, 16, 80));     // Pilar 2
        blocks.add(new TileBlock(778, 127, 30, 5));     // Plataforma 9
        blocks.add(new TileBlock(832, 127, 42, 5));     // Plataforma 10
        blocks.add(new TileBlock(924, 127, 176, 5));     // Plataforma 11
        blocks.add(new TileBlock(1002, 48, 16, 80));     // Pilar 2
        blocks.add(new TileBlock(1147, 127, 113, 5));     // Plataforma 11
        blocks.add(new TileBlock(1389, 0, 16, 48));    // Pilar***
        //blocks.add(new TileBlock(1280, 117, 48, 15));    // Plataforma 15
        //blocks.add(new TileBlock(1360, 117, 64, 15));    // Plataforma 16
        blocks.add(new TileBlock(1468, 118, 16, 95));    // Pilar lateral***
        //blocks.add(new TileBlock(1472, 117, 48, 15));    // Plataforma 17
        blocks.add(new TileBlock(1626, 127, 32, 5));    // Plataforma 18  *
        blocks.add(new TileBlock(1389, 64, 16, 48));    // Pilar**
        blocks.add(new TileBlock(1755, 118, 16, 95));    // Pilar lateral***
        blocks.add(new TileBlock(1680, 127, 50, 5));    // Plataforma 19
        //blocks.add(new TileBlock(1465, 230, 16, 64));    // Pilar lateral
        //blocks.add(new TileBlock(1776, 117, 48, 15));    // Plataforma 20
        blocks.add(new TileBlock(1915, 133, 16, 70));    // Pilar lateral***
        //blocks.add(new TileBlock(1888, 117, 48, 15));    // Plataforma 21
        blocks.add(new TileBlock(1915, 133, 65, 15));    // Plataforma final
        blocks.add(new TileBlock(2032, 0, 16, 222));    // Parede direita
    }

    public Array<TileBlock> getBlocks() {
        return blocks;
    }

    public void draw(SpriteBatch batch) {
        for (TileBlock block : blocks) {
            block.draw(batch);
        }
    }

    public static Blocks getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Blocks();
        }
        return INSTANCE;
    }
}