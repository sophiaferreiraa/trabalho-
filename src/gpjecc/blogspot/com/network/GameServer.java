package gpjecc.blogspot.com.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import gpjecc.blogspot.com.network.dto.GameSnapshot;
import gpjecc.blogspot.com.network.dto.LobbyState;
import gpjecc.blogspot.com.network.dto.PlayerInput;

public interface GameServer extends Remote {
    // Métodos de Jogo
    UUID join(String playerName, GameClientListener listener) throws RemoteException;
    void leave(UUID playerId) throws RemoteException;
    void sendInput(UUID playerId, PlayerInput input) throws RemoteException;
    GameSnapshot getSnapshot() throws RemoteException;

    // --- MÉTODOS DE LOBBY ---
    LobbyState getLobbyState() throws RemoteException;
    void hostStartGame() throws RemoteException;
    void setHostId(UUID hostPlayerId) throws RemoteException;
    
    // --- MÉTODO DE NÍVEL ---
    void loadNextLevel() throws RemoteException;
}