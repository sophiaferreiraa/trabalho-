package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.*;
import java.util.UUID;

public class RmiGameClient {
    private GameServer server;
    private RmiClientListener listener;
    public UUID playerId;

    // MODIFICAÇÃO: Aceita o nome do jogador
    public void connect(String serverHost, String playerName) throws Exception {
        server = (GameServer) java.rmi.Naming.lookup("rmi://" + serverHost + "/GameServer");
        listener = new RmiClientListener();
        
        // Envia o nome do jogador ao se juntar
        playerId = server.join(playerName, listener); 
    }

    public void sendInput(PlayerInput input) {
        if (server == null || playerId == null) {
            System.out.println("[RMI] Servidor ou playerId ainda não inicializado — ignorando input");
            return;
        }
        try {
            server.sendInput(playerId, input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GameSnapshot pollSnapshot() {
        if (listener == null) return null;
        return listener.snapshotQueue.poll();
    }
    
    // --- MÉTODOS DE LOBBY ADICIONADOS ---
    
    public LobbyState getLobbyState() {
        if (server == null) return null;
        try {
            return server.getLobbyState();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Retorna nulo em caso de falha de conexão
        }
    }
    
    public void hostStartGame() {
        if (server == null) return;
        try {
            server.hostStartGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ------------------------------------

    public void disconnect() {
        if (server == null || playerId == null) return;
        try {
            server.leave(playerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        server = null;
        playerId = null;
    }
}