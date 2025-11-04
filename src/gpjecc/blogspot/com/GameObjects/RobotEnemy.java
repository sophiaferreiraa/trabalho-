package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import gpjecc.blogspot.com.AssetsManager.AssetsManager;
import gpjecc.blogspot.com.GameObjects.Base.GameObject;

public class RobotEnemy extends GameObject {

    private static final float WIDTH = 48;
    private static final float HEIGHT = 48;
    private static final float FRAME_DURATION = 0.15f;

    private int health = 5;

    private Animation idleAnimation;
    private TextureRegion[][] frames;

    // Lista de todos os robôs no jogo
    public static Array<RobotEnemy> robots = new Array<>();

    public RobotEnemy(float x, float y) {
        super(new Vector2(x, y), new Vector2(WIDTH, HEIGHT), "Robot_enemy");

        // Pega os frames da imagem (3 frames horizontais)
        frames = AssetsManager.getInstance().getFrames("Robot_enemy", new Vector2(WIDTH, HEIGHT));

        // Supondo que as 3 animações estejam na primeira linha da sprite sheet
        idleAnimation = AssetsManager.getInstance().getAnimation(frames, 0, FRAME_DURATION);
    }

    public void update(float delta) {
        // Robôs ficam parados, mas ainda atualizam a animação
        currentFrame = AssetsManager.getInstance().getCurrentFrame(idleAnimation);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
    }

    public static void updateAll(float delta) {
        for (RobotEnemy r : robots) {
            if (r.isAlive()) {
                r.update(delta);
            }
        }
    }

    public static void drawAll(SpriteBatch batch) {
        for (RobotEnemy r : robots) {
            if (r.isAlive()) {
                r.draw(batch);
            }
        }
    }

    public static void createRobots(int level) {
    robots.clear();

    if (level == 1) {
        // Robôs parados no andar de cima
        robots.add(new RobotEnemy(400, 170));
        robots.add(new RobotEnemy(900, 170));
        robots.add(new RobotEnemy(1400, 170));
    } else if (level == 2) {
        // Robôs da fase 2 (andar superior)
        robots.add(new RobotEnemy(350, 140));
        robots.add(new RobotEnemy(750, 140));
        robots.add(new RobotEnemy(1150, 140));
        robots.add(new RobotEnemy(1550, 140));
    }
}


    public static void disposeAll() {
        robots.clear();
    }
}
