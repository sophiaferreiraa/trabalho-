package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.*;

public class GameServerImpl extends UnicastRemoteObject implements GameServer {
    private static final long serialVersionUID = 1L;

    private final Map<UUID, PlayerState> players = new ConcurrentHashMap<>();
    private final Map<UUID, GameClientListener> listeners = new ConcurrentHashMap<>();
    private final Queue<QueuedInput> inputQueue = new ConcurrentLinkedQueue<>();

    private final ScheduledExecutorService tickExecutor = Executors.newSingleThreadScheduledExecutor();
    
    // --- ESTADO DO JOGO E LOBBY ---
    private String gameState = "LOBBY"; // Inicia no Lobby
    private UUID hostId = null;
    // -------------------------------
    
    private final int TICK_RATE = 20; 
    private final float TICK_DELTA = 1.0f / TICK_RATE; 
    private final float GRAVITY = -500f; // Gravidade (puxa para baixo)
    private final float JUMP_IMPULSE = 300f; // Força do pulo (puxa para cima)
    private static final float PLAYER_WIDTH = 23;  
    private static final float PLAYER_HEIGHT = 45; 
    private static final float GROUND_Y = 50f; // O chão do seu mapa

    protected GameServerImpl() throws RemoteException {
        super();
        long period = 1000L / TICK_RATE;
        tickExecutor.scheduleAtFixedRate(this::tick, 0, period, TimeUnit.MILLISECONDS);
        System.out.println("[GameServer] Iniciado tick loop a " + TICK_RATE + " tps");
    }

    // --- Implementação dos Métodos de Lobby ---

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
        // TODO: Adicionar verificação se quem chamou é o hostId
        System.out.println("[GameServer] Host iniciou o jogo! Mudando estado para IN_GAME.");
        this.gameState = "IN_GAME";
    }

    // --- Implementação dos Métodos de Jogo ---

    @Override
    public UUID join(String playerName, GameClientListener listener) throws RemoteException {
        UUID id = UUID.randomUUID();

        PlayerState state = new PlayerState();
        state.playerId = id; 
        state.name = playerName;
        state.x = 100f;
        state.y = GROUND_Y + 50f; 
        state.vx = 0f;
        state.vy = 0f;
        state.hp = 5f; 
        state.facingRight = true;
        
        state.isAttacking = false;
        state.attackCooldown = 0f;
        state.damageCooldown = 0f;

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
        // TODO: Adicionar lógica para lidar se o Host sair
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

    private void tick() {
        try {
            // --- GUARDA DO JOGO ---
            // SÓ PROCESSA A FÍSICA SE O ESTADO FOR "IN_GAME"
            if (!"IN_GAME".equals(this.gameState)) {
                return; // Não faz nada (fica em modo lobby)
            }
            // ---------------------
        
            QueuedInput qi;
            while ((qi = inputQueue.poll()) != null) {
                applyInput(qi.playerId, qi.input);
            }

            physicsStep();

            GameSnapshot snap = buildSnapshot();
            for (Map.Entry<UUID, GameClientListener> e : listeners.entrySet()) {
                try {
                    e.getValue().onSnapshot(snap.id, snap);
                } catch (Exception ex) {
                    System.err.println("Falha ao notificar cliente " + e.getKey() + ": " + ex.getMessage());
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    private void applyInput(UUID playerId, PlayerInput input) {
        PlayerState ps = players.get(playerId);
        if (ps == null) return;

        float speed = 4.0f; 

        // horizontal
        if (input.left && !input.right) {
            ps.vx = -speed;
            ps.facingRight = false;
        } else if (input.right && !input.left) {
            ps.vx = speed;
            ps.facingRight = true;
        } else {
            ps.vx = 0f;
        }

        // Pulo (só pula se estiver no chão)
        if (input.jump && Math.abs(ps.y - GROUND_Y) < 1.0f) { // 1.0f de margem
            ps.vy = JUMP_IMPULSE / TICK_RATE; // Aplica impulso (ajustado para tick)
        }
        
        if (input.attack && ps.attackCooldown <= 0) {
            ps.isAttacking = true; 
            ps.attackCooldown = 0.5f; 
        }
    }
    
    private boolean checkCollision(PlayerState p1, PlayerState p2) {
        return p1.x < p2.x + PLAYER_WIDTH &&
               p1.x + PLAYER_WIDTH > p2.x &&
               p1.y < p2.y + PLAYER_HEIGHT &&
               p1.y + PLAYER_HEIGHT > p2.y;
    }

    private void physicsStep() {
        
        // 1. Atualiza física (posição)
        for (PlayerState ps : players.values()) {
            ps.vy += GRAVITY * TICK_DELTA; 
            ps.x += ps.vx;
            ps.y += ps.vy * TICK_DELTA;

            if (ps.y < GROUND_Y) { 
                ps.y = GROUND_Y;
                ps.vy = 0f;
            }
            if (ps.x < 0) ps.x = 0;
        }
        
        // 2. Processa Interações (Combate PvP)
        for (PlayerState attacker : players.values()) {
            if (attacker.isAttacking) {
                for (PlayerState target : players.values()) {
                    if (attacker.playerId.equals(target.playerId)) continue; 

                    if (checkCollision(attacker, target) && target.damageCooldown <= 0) {
                        target.hp -= 0.5f; 
                        target.damageCooldown = 1.0f; 
                        if (target.hp <= 0) {
                            target.hp = 0;
                        }
                        System.out.println("[Server] HIT! " + attacker.name + " acertou " + target.name + " (HP: " + target.hp + ")");
                    }
                }
                attacker.isAttacking = false; 
            }
        }
        
        // 3. Atualiza todos os Cooldowns
        for (PlayerState ps : players.values()) {
            if (ps.attackCooldown > 0) {
                ps.attackCooldown -= TICK_DELTA;
            }
            if (ps.damageCooldown > 0) {
                ps.damageCooldown -= TICK_DELTA;
            }
        }
    }

    private GameSnapshot buildSnapshot() {
        GameSnapshot s = new GameSnapshot();
        s.id = UUID.randomUUID();
        s.serverTimestamp = System.currentTimeMillis();
        s.players = new HashMap<>();

        for (Map.Entry<UUID, PlayerState> e : players.entrySet()) {
            UUID id = e.getKey();
            PlayerState ps = e.getValue();
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
            s.players.put(id, clone);
        }
        return s;
    }

    private static class QueuedInput {
        UUID playerId;
        PlayerInput input;
        QueuedInput(UUID pid, PlayerInput i) { this.playerId = pid; this.input = i; }
    }
}