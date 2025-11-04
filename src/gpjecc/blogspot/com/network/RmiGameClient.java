// RmiGameClient.java
package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.*;
import java.util.UUID;

public class RmiGameClient {
    private GameServer server;
    private RmiClientListener listener;
    public UUID playerId;

    public void connect(String serverHost) throws Exception {
        server = (GameServer) java.rmi.Naming.lookup("rmi://" + serverHost + "/GameServer");
        listener = new RmiClientListener();
        playerId = server.join("Jogador", listener);
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

    public gpjecc.blogspot.com.network.dto.GameSnapshot pollSnapshot() {
        if (listener == null) return null;
        return listener.snapshotQueue.poll();
    }

    public void disconnect() {
        try {
            server.leave(playerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
