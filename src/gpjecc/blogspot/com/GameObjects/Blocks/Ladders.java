// sophiaferreiraa/trabalho-/trabalho--02347792bae4ea97c472a8e845b9c8792d2d1ef5/src/gpjecc/blogspot/com/GameObjects/Blocks/Ladders.java
package gpjecc.blogspot.com.GameObjects.Blocks;

import com.badlogic.gdx.utils.Array;

public class Ladders {
    private Array<LadderBlock> ladders;

    // Construtor público que aceita o nível
    public Ladders(int level) {
        ladders = new Array<LadderBlock>();

        // Posição Y do chão = 50
        // Posição Y das plataformas = 125
        float groundY = 50;
        float platformY = 125;
        float ladderHeight = platformY - groundY; // Altura da escada

        // --- MUDANÇA: SÓ CARREGA ESCADAS SE O NÍVEL FOR VÁLIDO ---
        if (level == 1) {
            // Layout Nível 1
            ladders.add(new LadderBlock(204, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(330, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(814, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(1663, groundY, 15, ladderHeight, platformY));

        } else if (level == 2) {
            // Layout Nível 2
            ladders.add(new LadderBlock(260, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(460, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(660, groundY, 15, ladderHeight, platformY));
            ladders.add(new LadderBlock(860, groundY, 15, ladderHeight, platformY));
        }
        // Se 'level' não for 1 ou 2, o array 'ladders' ficará vazio.
        // --- FIM DA MUDANÇA ---
    }

    public Array<LadderBlock> getLadders() {
        return ladders;
    }
}