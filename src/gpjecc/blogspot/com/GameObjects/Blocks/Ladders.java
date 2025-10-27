package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.utils.Array;

public class Ladders {
    private static Ladders INSTANCE = null;
    private Array<LadderBlock> ladders;

    private Ladders() {
        ladders = new Array<LadderBlock>();
        
        // Posição Y do chão = 50
        // Posição Y das plataformas = 125
        float groundY = 50;
        float platformY = 125;
        float ladderHeight = platformY - groundY; // Altura da escada

        // Adicione as escadas aqui (x, y, largura, altura, y_do_topo)
        // Você precisará ajustar os valores de X e LARGURA para cada escada
        
        // Escada 1 (perto do início)
        ladders.add(new LadderBlock(204, groundY, 15, ladderHeight, platformY));
        
        // Escada 2
        ladders.add(new LadderBlock(330, groundY, 15, ladderHeight, platformY));

        // Escada 3
        ladders.add(new LadderBlock(814, groundY, 15, ladderHeight, platformY));
        // Escada 4
        ladders.add(new LadderBlock(1663, groundY, 15, ladderHeight, platformY));

        // Adicione as outras escadas do mapa aqui...
    }

    public Array<LadderBlock> getLadders() {
        return ladders;
    }

    /*public void draw(SpriteBatch batch) {
        for (LadderBlock ladder : ladders) {
            ladder.draw(batch);
        }
    }*/

    public static Ladders getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ladders();
        }
        return INSTANCE;
    }
}