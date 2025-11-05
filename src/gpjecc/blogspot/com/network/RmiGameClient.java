package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.*;
import java.util.UUID;

public class RmiGameClient {
    private GameServer server;
    private RmiClientListener listener;
    public UUID playerId;

    private static final int RMI_PORT = 1100;

    public void connect(String serverHost, String playerName) throws Exception {
        
        String lookupUrl = "rmi://" + serverHost + ":" + RMI_PORT + "/GameServer";
        System.out.println("[RmiGameClient] Conectando em: " + lookupUrl);
        
        server = (GameServer) java.rmi.Naming.lookup(lookupUrl);
        listener = new RmiClientListener();
        
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
            if (e instanceof java.rmi.ConnectException || e instanceof java.rmi.RemoteException) {
                System.err.println("Erro de conexão ao enviar input. A conexão pode ter caído.");
                server = null; // Força a parada
            }
        }
    }

    public GameSnapshot pollSnapshot() {
        if (listener == null) return null;
        return listener.snapshotQueue.poll();
    }
    
    // --- MÉTODOS DE LOBBY ---
    
    public LobbyState getLobbyState() {
        if (server == null) return null;
        try {
            return server.getLobbyState();
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
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
    
    // --- MÉTODO ADICIONADO (CORREÇÃO DO ERRO) ---
    public void loadNextLevel() {
        if (server == null) return;
        try {
            // Chama o método no servidor
            server.loadNextLevel();
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