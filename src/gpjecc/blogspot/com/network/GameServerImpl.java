package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.*;
import gpjecc.blogspot.com.network.world.ServerBlocks;
import gpjecc.blogspot.com.network.world.ServerLadders;
import gpjecc.blogspot.com.network.world.ServerRectangle;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {
    private static final long serialVersionUID = 1L;

    // --- LISTAS DE ESTADO DO SERVIDOR ---
    private final Map<UUID, PlayerState> players = new ConcurrentHashMap<>();
    private final Map<UUID, EnemyState> enemies = new ConcurrentHashMap<>(); 
    private final Map<UUID, GameClientListener> listeners = new ConcurrentHashMap<>();
    private final Queue<QueuedInput> inputQueue = new ConcurrentLinkedQueue<>();
    
    // --- LÓGICA DO MUNDO DO JOGO ---
    private ServerBlocks serverBlocks;
    private ServerLadders serverLadders;

    private final ScheduledExecutorService tickExecutor = Executors.newSingleThreadScheduledExecutor();
    private String gameState = "LOBBY"; 
    private UUID hostId = null;
    
    // --- CONSTANTES DE JOGO (Tiradas do seu Player.java e Enemy.java) ---
    private final int TICK_RATE = 20; 
    private final float TICK_DELTA = 1.0f / TICK_RATE; 
    private static final float GRAVITY = -500f;
    private static final float JUMP_VELOCITY = 250f;
    private static final float MOVE_SPEED = 150f;
    private static final float CLIMB_SPEED = 100f;
    private static final float ENEMY_SPEED = 75f;
    
    private static final float PLAYER_WIDTH = 23;  
    private static final float PLAYER_HEIGHT = 45; 
    private static final float ENEMY_WIDTH = 32;
    private static final float ENEMY_HEIGHT = 48;
    
    // --- VARIÁVEL QUE ESTAVA FALTANDO ---
    private static final float GROUND_Y = 50f; 

    protected GameServerImpl(int port) throws RemoteException {
        super(port); // Força o RMI a usar esta porta
        
        long period = 1000L / TICK_RATE;
        tickExecutor.scheduleAtFixedRate(this::tick, 0, period, TimeUnit.MILLISECONDS);
        System.out.println("[GameServer] Iniciado tick loop a " + TICK_RATE + " tps");
    }

    // --- MÉTODOS DE LOBBY ---
    @Override
    public synchronized void setHostId(UUID hostPlayerId) throws RemoteException {
        if (this.hostId == null) { 
            this.hostId = hostPlayerId;
            System.out.println("[GameServer] Jogador " + hostPlayerId + " foi definido como Host.");
        }
    }
    
    @Override
    public synchronized LobbyState getLobbyState() throws RemoteException {
        LobbyState state = new LobbyState();
        state.gameState = this.gameState;
        state.hostId = this.hostId;
        Map<UUID, String> playerNames = new HashMap<>();
        for (PlayerState ps : players.values()) {
            playerNames.put(ps.playerId, ps.name);
        }
        state.playerNames = playerNames;
        return state;
    }

    @Override
    public synchronized void hostStartGame() throws RemoteException {
        if (gameState.equals("IN_GAME")) return; 

        System.out.println("[GameServer] Host iniciou o jogo! Criando nível 1...");
        this.gameState = "IN_GAME";
        
        this.serverBlocks = new ServerBlocks(1);
        this.serverLadders = new ServerLadders(1);
        
        enemies.clear();
        createEnemy(150, 50, 150, 250);
        createEnemy(300, 50, 250, 350);
        createEnemy(600, 50, 550, 650);
        createEnemy(900, 50, 850, 950);
        createEnemy(1200, 50, 1150, 1250);
        createEnemy(1500, 50, 1450, 1550);
        createEnemy(1800, 50, 1750, 1850);
    }
    
    private void createEnemy(float x, float y, float patrolStart, float patrolEnd) {
        UUID id = UUID.randomUUID();
        EnemyState enemy = new EnemyState(id, x, y, 3, patrolStart, patrolEnd);
        enemies.put(id, enemy);
    }

    // --- MÉTODOS DE JOGO ---
    @Override
    public UUID join(String playerName, GameClientListener listener) throws RemoteException {
        UUID id = UUID.randomUUID();
        PlayerState state = new PlayerState();
        state.playerId = id; 
        state.name = playerName;
        state.x = 100f; 
        state.y = 100f; 
        state.vx = 0f;
        state.vy = 0f;
        state.hp = 5f; 
        state.score = 0;
        state.facingRight = true;
        state.isDead = false;
        state.onGround = false;
        state.isClimbing = false;
        
        players.put(id, state);
        listeners.put(id, listener);
        System.out.println("Player joined: " + playerName + " -> " + id);
        return id;
    }

    @Override
    public void leave(UUID playerId) throws RemoteException {
        players.remove(playerId);
        listeners.remove(playerId);
        System.out.println("Player left: " + playerId);
    }

    @Override
    public void sendInput(UUID playerId, PlayerInput input) throws RemoteException {
        if (playerId == null || input == null) return;
        if (!players.containsKey(playerId)) return;
        inputQueue.add(new QueuedInput(playerId, input));
    }

    @Override
    public GameSnapshot getSnapshot() throws RemoteException {
        return buildSnapshot();
    }

    // //////////////////////////////////////////////////////
    // /// LOOP PRINCIPAL DO JOGO (TICK)
    // //////////////////////////////////////////////////////
    private void tick() {
        try {
            if (!"IN_GAME".equals(this.gameState)) {
                return; // Não processa física se estiver no Lobby
            }
        
            Map<UUID, PlayerInput> lastInputs = processInputs();
            physicsStep(lastInputs); // Passa os inputs para a física

            GameSnapshot snap = buildSnapshot();
            for (GameClientListener listener : listeners.values()) {
                try {
                    listener.onSnapshot(snap.id, snap);
                } catch (Exception ex) { /* Cliente desconectou */ }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private Map<UUID, PlayerInput> processInputs() {
        Map<UUID, PlayerInput> inputsByPlayer = new HashMap<>();
        
        QueuedInput qi;
        while ((qi = inputQueue.poll()) != null) {
            inputsByPlayer.put(qi.playerId, qi.input);
        }
        
        for(Map.Entry<UUID, PlayerInput> entry : inputsByPlayer.entrySet()) {
            PlayerState ps = players.get(entry.getKey());
            PlayerInput input = entry.getValue();
            if (ps == null || ps.isDead) continue; 

            // Movimento Horizontal
            if (input.left && !input.right) {
                ps.vx = -MOVE_SPEED;
                ps.facingRight = false;
            } else if (input.right && !input.left) {
                ps.vx = MOVE_SPEED;
                ps.facingRight = true;
            } else {
                ps.vx = 0f;
            }
            
            // Ataque
            if (input.attack && ps.attackCooldown <= 0) {
                ps.isAttacking = true; 
                ps.attackCooldown = 0.5f; 
            }
        }
        return inputsByPlayer;
    }
    
    private boolean checkCollision(float x1, float y1, float w1, float h1, float x2, float y2, float w2, float h2) {
        return x1 < x2 + w2 &&
               x1 + w1 > x2 &&
               y1 < y2 + h2 &&
               y1 + h1 > y2;
    }

    private void physicsStep(Map<UUID, PlayerInput> lastInputs) {
        
        if (serverBlocks == null) { 
            System.err.println("ServerBlocks não foi inicializado!");
            return;
        }

        // --- 1. ATUALIZA JOGADORES ---
        for (PlayerState ps : players.values()) {
            if (ps.isDead) continue;
            
            PlayerInput input = lastInputs.get(ps.playerId); 
            boolean inputUp = input != null && input.up;
            boolean inputDown = input != null && input.down;
            boolean inputJump = input != null && input.jump;

            // --- Lógica de Escada (Copiado do seu Player.java original) ---
            ServerRectangle playerRect = new ServerRectangle(ps.x, ps.y, PLAYER_WIDTH, PLAYER_HEIGHT);
            boolean onLadder = false;
            
            for (ServerRectangle ladderRect : serverLadders.getLadders()) {
                if (playerRect.overlaps(ladderRect)) {
                    onLadder = true;
                    if (inputUp && !ps.isClimbing) { 
                        ps.isClimbing = true;
                        ps.x = ladderRect.x + (ladderRect.width / 2) - (PLAYER_WIDTH / 2); // Centraliza
                        ps.vy = 0;
                    }
                    break;
                }
            }
            
            if (!onLadder && ps.isClimbing) {
                ps.isClimbing = false;
            }
            
            // --- Física de Pulo, Gravidade e Escada ---
            if (ps.isClimbing) {
                ps.vy = 0;
                if (inputUp) {
                    ps.y += CLIMB_SPEED * TICK_DELTA;
                } else if (inputDown) {
                    ps.y -= CLIMB_SPEED * TICK_DELTA;
                }
                
                if (ps.y >= 125) { // Pousa no topo da escada (plataforma)
                    ps.y = 125;
                    ps.isClimbing = false;
                    ps.onGround = true;
                }
            } else {
                // Física de Pulo e Gravidade (Normal)
                if (inputJump && ps.onGround) {
                    ps.vy = JUMP_VELOCITY;
                    ps.onGround = false;
                }
                
                ps.vy += GRAVITY * TICK_DELTA;
            }
            
            // Aplica velocidade X e Y
            ps.x += ps.vx * TICK_DELTA;
            ps.y += ps.vy * TICK_DELTA;
            
            // --- Colisão com Blocos (baseado no seu Player.java) ---
            ps.onGround = false;
            playerRect.x = ps.x; // Atualiza o retângulo para a nova posição
            playerRect.y = ps.y;

            for (ServerRectangle blockRect : serverBlocks.getBlocks()) {
                if (playerRect.overlaps(blockRect)) {
                    float playerBottom = ps.y;
                    float playerTop = ps.y + PLAYER_HEIGHT;
                    float playerLeft = ps.x;
                    float playerRight = ps.x + PLAYER_WIDTH;
                    float blockBottom = blockRect.y;
                    float blockTop = blockRect.y + blockRect.height;
                    float blockLeft = blockRect.x;
                    float blockRight = blockRect.x + blockRect.width;

                    if (ps.vy <= 0 && playerBottom < blockTop && playerTop > blockTop && playerRight > blockLeft && playerLeft < blockRight) {
                        ps.y = blockTop;
                        ps.vy = 0;
                        ps.onGround = true;
                    }
                    else if (ps.vy > 0 && playerTop > blockBottom && playerBottom < blockBottom) {
                        ps.y = blockBottom - PLAYER_HEIGHT;
                        ps.vy = 0;
                    }
                    else if (playerRight > blockLeft && playerLeft < blockLeft && playerTop > blockBottom && playerBottom < blockTop && ps.vx > 0) {
                        ps.x = blockLeft - PLAYER_WIDTH;
                        ps.vx = 0;
                    } else if (playerLeft < blockRight && playerRight > blockRight && playerTop > blockBottom && playerBottom < blockTop && ps.vx < 0) {
                        ps.x = blockRight;
                        ps.vx = 0;
                    }
                }
            }

            // Atualiza Cooldowns e estado de ataque
            if (ps.attackCooldown > 0) {
                ps.attackCooldown -= TICK_DELTA;
            } else if (ps.isAttacking) {
                ps.isAttacking = false; // O ataque dura o tempo do cooldown
            }
            if (ps.damageCooldown > 0) ps.damageCooldown -= TICK_DELTA;
            
            if (ps.hp <= 0) {
                ps.isDead = true;
                ps.vx = 0;
                ps.vy = 0;
            }
        }

        // --- 2. ATUALIZA INIMIGOS ---
        List<UUID> enemiesToRemove = new ArrayList<>();
        for (EnemyState es : enemies.values()) {
            if (es.health <= 0) {
                enemiesToRemove.add(es.id);
                continue;
            }
            
            // Lógica de IA (Patrulha - copiado de Enemy.java)
            float speed = ENEMY_SPEED * TICK_DELTA;
            
            if (es.direction == 1) { // Indo para a direita
                es.x += speed;
                es.facingRight = true;
                if (es.x > es.patrolEnd) es.direction = -1;
            } else { // Indo para a esquerda
                es.x -= speed;
                es.facingRight = false;
                if (es.x < es.patrolStart) es.direction = 1;
            }
            es.y = GROUND_Y; // Mantém no chão
        }
        for (UUID id : enemiesToRemove) {
            enemies.remove(id);
        }

        // --- 3. PROCESSA COLISÕES DE COMBATE ---
        for (PlayerState player : players.values()) {
            if (player.isDead) continue;

            // 3a. Colisão Jogador vs Inimigo (PvE)
            for (EnemyState enemy : enemies.values()) {
                if (enemy.health <= 0) continue;
                
                boolean isOverlapping = checkCollision(
                    player.x, player.y, PLAYER_WIDTH, PLAYER_HEIGHT,
                    enemy.x, enemy.y, ENEMY_WIDTH, ENEMY_HEIGHT
                );

                if (isOverlapping) {
                    if (player.isAttacking) {
                        enemy.health -= 1; 
                        if(enemy.health <= 0) {
                            player.score += 100; 
                        }
                    } else if (player.damageCooldown <= 0) {
                        player.hp -= 0.5f;
                        player.damageCooldown = 1.0f; 
                    }
                }
            }
            
            // 3b. Colisão Jogador vs Jogador (PvP)
            if (player.isAttacking) {
                for (PlayerState target : players.values()) {
                    if (player.playerId.equals(target.playerId) || target.isDead) continue; 

                    boolean isOverlapping = checkCollision(
                        player.x, player.y, PLAYER_WIDTH, PLAYER_HEIGHT,
                        target.x, target.y, PLAYER_WIDTH, PLAYER_HEIGHT
                    );

                    if (isOverlapping && target.damageCooldown <= 0) {
                        target.hp -= 0.5f; 
                        target.damageCooldown = 1.0f; 
                    }
                }
            }
        }
    }

    private GameSnapshot buildSnapshot() {
        GameSnapshot s = new GameSnapshot();
        s.id = UUID.randomUUID();
        s.serverTimestamp = System.currentTimeMillis();
        
        Map<UUID, PlayerState> playersCopy = new HashMap<>();
        for (PlayerState ps : players.values()) {
            playersCopy.put(ps.playerId, clonePlayerState(ps));
        }
        s.players = playersCopy;
        
        Map<UUID, EnemyState> enemiesCopy = new HashMap<>();
         for (EnemyState es : enemies.values()) {
            enemiesCopy.put(es.id, cloneEnemyState(es));
        }
        s.enemies = enemiesCopy;

        return s;
    }
    
    private PlayerState clonePlayerState(PlayerState ps) {
        PlayerState clone = new PlayerState();
        clone.playerId = ps.playerId; 
        clone.name = ps.name;
        clone.x = ps.x;
        clone.y = ps.y;
        clone.vx = ps.vx;
        clone.vy = ps.vy;
        clone.hp = ps.hp;
        clone.facingRight = ps.facingRight;
        clone.isAttacking = ps.isAttacking;
        clone.score = ps.score;
        clone.onGround = ps.onGround;
        clone.isClimbing = ps.isClimbing;
        clone.isDead = ps.isDead;
        return clone;
    }
    
    private EnemyState cloneEnemyState(EnemyState es) {
         EnemyState clone = new EnemyState();
         clone.id = es.id;
         clone.x = es.x;
         clone.y = es.y;
         clone.health = es.health;
         clone.facingRight = es.facingRight;
         clone.patrolStart = es.patrolStart;
         clone.patrolEnd = es.patrolEnd;
         clone.direction = es.direction;
         return clone;
    }

    private static class QueuedInput {
        UUID playerId;
        PlayerInput input;
        QueuedInput(UUID pid, PlayerInput i) { this.playerId = pid; this.input = i; }
    }
}