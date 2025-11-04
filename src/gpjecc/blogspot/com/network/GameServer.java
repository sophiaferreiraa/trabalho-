package gpjecc.blogspot.com.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import gpjecc.blogspot.com.network.dto.GameSnapshot;
import gpjecc.blogspot.com.network.dto.PlayerInput;

public interface GameServer extends Remote {
    UUID join(String playerName, GameClientListener listener) throws RemoteException;

    void leave(UUID playerId) throws RemoteException;
    void sendInput(UUID playerId, PlayerInput input) throws RemoteException;
    GameSnapshot getSnapshot() throws RemoteException;
}
