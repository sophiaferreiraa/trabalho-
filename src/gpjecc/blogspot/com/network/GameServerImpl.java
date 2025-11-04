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
    private final int TICK_RATE = 20; 
    private final float GRAVITY = 0.6f;
    private final float GROUND_Y = 300f;

    protected GameServerImpl() throws RemoteException {
        super();
        long period = 1000L / TICK_RATE;
        tickExecutor.scheduleAtFixedRate(this::tick, 0, period, TimeUnit.MILLISECONDS);
        System.out.println("[GameServer] Iniciado tick loop a " + TICK_RATE + " tps");
    }

    @Override
    public UUID join(String playerName, GameClientListener listener) throws RemoteException {
        UUID id = UUID.randomUUID();

        PlayerState state = new PlayerState();
        state.name = playerName;
        state.x = 100f;
        state.y = GROUND_Y;
        state.vx = 0f;
        state.vy = 0f;
        state.hp = 100;
        state.facingRight = true;

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
        if (playerId == null || input == null)
            return;
        if (!players.containsKey(playerId))
            return;
        inputQueue.add(new QueuedInput(playerId, input));
    }

    @Override
    public GameSnapshot getSnapshot() throws RemoteException {
        return buildSnapshot();
    }

    private void tick() {
        try {
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
        if (ps == null)
            return;

        float speed = 4.0f;
        float jumpImpulse = -10f;

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

        if (input.jump && Math.abs(ps.y - GROUND_Y) < 0.01f) {
            ps.vy = jumpImpulse;
        }

    }

    private void physicsStep() {
        for (PlayerState ps : players.values()) {
            ps.vy += GRAVITY;

            ps.x += ps.vx;
            ps.y += ps.vy;

            if (ps.y > GROUND_Y) {
                ps.y = GROUND_Y;
                ps.vy = 0f;
            }

            if (ps.x < 0)
                ps.x = 0;
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
            clone.name = ps.name;
            clone.x = ps.x;
            clone.y = ps.y;
            clone.vx = ps.vx;
            clone.vy = ps.vy;
            clone.hp = ps.hp;
            clone.facingRight = ps.facingRight;

            s.players.put(id, clone);
        }

        return s;
    }

    private static class QueuedInput {
        UUID playerId;
        PlayerInput input;

        QueuedInput(UUID pid, PlayerInput i) {
            this.playerId = pid;
            this.input = i;
        }
    }
}
