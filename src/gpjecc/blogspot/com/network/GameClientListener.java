package gpjecc.blogspot.com.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import gpjecc.blogspot.com.network.dto.GameSnapshot;

public interface GameClientListener extends Remote {
    void onSnapshot(UUID snapshotId, GameSnapshot snapshot) throws RemoteException;

    void onSystemMessage(String message) throws RemoteException;
}
