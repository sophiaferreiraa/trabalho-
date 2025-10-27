package gpjecc.blogspot.com.GameObjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import gpjecc.blogspot.com.GameObjects.Base.GameObject;

public class Enemy extends GameObject {

    private int health = 3; // Vida padrão do inimigo
    private float patrolStart;
    private float patrolEnd;

    // Constantes
    public static final float WIDTH = 32;
    public static final float HEIGHT = 48;
    public static final float SPEED = 75f;

    // Variáveis de Estado
    private int direction = 1; // 1 = direita, -1 = esquerda
    private float patrolRange = 100f;

    // Lista estática para controlar todos os inimigos
    public static Array<Enemy> enemies = new Array<>();

    public Enemy(float x, float y) {
        super(new Vector2(x, y), new Vector2(WIDTH, HEIGHT), "inimigo_Fase1migo");
        this.patrolStart = x - patrolRange;
        this.patrolEnd = x + patrolRange;
    }

    public Enemy(float x, float y, float patrolStart, float patrolEnd) {
        super(new Vector2(x, y), new Vector2(WIDTH, HEIGHT), "inimigo_Fase1migo");
        this.patrolStart = patrolStart;
        this.patrolEnd = patrolEnd;
    }

    public void update(float delta) {
        position.x += SPEED * direction * delta;

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
        batch.draw(texture, position.x, position.y, WIDTH, HEIGHT);
    }

    public void dispose() {
        texture.dispose();
    }

    // Métodos para gerenciar todos os inimigos -------------------

    // Atualiza todos os inimigos
    public static void updateAll(float delta) {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.update(delta);
            }
        }
    }

    // Desenha todos os inimigos
    public static void drawAll(SpriteBatch batch) {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                e.draw(batch);
            }
        }
    }

    // Remove e limpa todos os inimigos
    public static void disposeAll() {
        for (Enemy e : enemies) {
            e.dispose();
        }
        enemies.clear();
    }

    // Inicializa os inimigos no mapa
    public static void createDefaultEnemies() {
        enemies.clear(); // Garante que não duplica
        enemies.add(new Enemy(150, 50));                    // Posição 1
        enemies.add(new Enemy(300, 50, 250, 350));          // Posição 2
        enemies.add(new Enemy(600, 50, 550, 650));          // Posição 3
        enemies.add(new Enemy(900, 50, 850, 950));          // Posição 4
        enemies.add(new Enemy(1200, 50, 1150, 1250));       // Posição 5
        enemies.add(new Enemy(1500, 50, 1450, 1550));       // Posição 6
        enemies.add(new Enemy(1800, 50, 1750, 1850));       // Posição 7
    }
}