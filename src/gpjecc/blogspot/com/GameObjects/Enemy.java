// sophiaferreiraa/trabalho-/trabalho--0534c1e57f69de4e39b35eda93a3dec3369520db/src/gpjecc/blogspot/com/GameObjects/Enemy.java
package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import gpjecc.blogspot.com.GameObjects.Base.GameObject;

public class Enemy extends GameObject {

    private int health = 3;
    private float patrolStart;
    private float patrolEnd;

    // Constantes
    public static final float WIDTH = 32;
    public static final float HEIGHT = 48;
    public static final float DEFAULT_SPEED = 75f;

    // Variáveis de Estado
    private int direction = 1;
    // private float patrolRange = 100f; // REMOVIDO (não estava sendo usado)
    private float speed;

    // Lista estática para controlar todos os inimigos
    public static Array<Enemy> enemies = new Array<>();

    // Construtor padrão (Nível 1)
    public Enemy(float x, float y) {
        this(x, y, x - 100, x + 100, DEFAULT_SPEED, "inimigo_Fase1migo");
    }

    // Construtor com patrulha (Nível 1)
    public Enemy(float x, float y, float patrolStart, float patrolEnd) {
        this(x, y, patrolStart, patrolEnd, DEFAULT_SPEED, "inimigo_Fase1migo");
    }

    // Construtor completo (usado para Nível 2)
    public Enemy(float x, float y, float patrolStart, float patrolEnd, float speed, String textureKey) {
        super(new Vector2(x, y), new Vector2(WIDTH, HEIGHT), textureKey);
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
        this.speed = speed;
    }

    public void update(float delta) {
        // Usa a velocidade da instância, não a estática
        position.x += speed * direction * delta;

        if (position.x > patrolEnd) {
            direction = -1;
        } else if (position.x < patrolStart) {
            direction = 1;
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void draw(SpriteBatch batch) {
        // ATUALIZADO para desenhar o frame de animação (como no Player)
        // Se o inimigo não tiver animação, currentFrame será o primeiro frame
        batch.draw(currentFrame, position.x, position.y, WIDTH, HEIGHT);
    }

    public void dispose() {
        // texture.dispose(); // Gerenciado pelo AssetsManager
    }

    // Métodos para gerenciar todos os inimigos -------------------

    public static void updateAll(float delta) {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.update(delta);
            }
        }
    }

    public static void drawAll(SpriteBatch batch) {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.draw(batch);
            }
        }
    }

    public static void disposeAll() {
        enemies.clear();
    }

    public static void createEnemies(int level) {
        enemies.clear();

        if (level == 1) {
            enemies.add(new Enemy(150, 50));
            enemies.add(new Enemy(300, 50, 250, 350));
            enemies.add(new Enemy(600, 50, 550, 650));
            enemies.add(new Enemy(900, 50, 850, 950));
            enemies.add(new Enemy(1200, 50, 1150, 1250));
            enemies.add(new Enemy(1500, 50, 1450, 1550));
            enemies.add(new Enemy(1800, 50, 1750, 1850));
        } else if (level == 2) {
            float fastSpeed = DEFAULT_SPEED * 1.5f;
            String tex = "inimigo_fase2";

            enemies.add(new Enemy(200, 50, 150, 300, fastSpeed, tex));
            enemies.add(new Enemy(450, 50, 400, 550, fastSpeed, tex));
            enemies.add(new Enemy(700, 50, 650, 800, fastSpeed, tex));
            enemies.add(new Enemy(950, 50, 900, 1050, fastSpeed, tex));
            enemies.add(new Enemy(1200, 50, 1150, 1300, fastSpeed, tex));
            enemies.add(new Enemy(1450, 50, 1400, 1550, fastSpeed, tex));
            enemies.add(new Enemy(1700, 50, 1650, 1800, fastSpeed, tex));
            enemies.add(new Enemy(1950, 50, 1900, 2050, fastSpeed, tex));
        }
    }
}