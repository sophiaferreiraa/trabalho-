package gpjecc.blogspot.com.network;

import gpjecc.blogspot.com.network.dto.GameSnapshot;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RmiClientListener extends UnicastRemoteObject implements GameClientListener {
    private static final long serialVersionUID = 1L;

    public final ConcurrentLinkedQueue<GameSnapshot> snapshotQueue = new ConcurrentLinkedQueue<>();

    protected RmiClientListener() throws RemoteException {
        super();
    }

    @Override
    public void onSnapshot(java.util.UUID snapshotId, GameSnapshot snapshot) throws RemoteException {

        snapshotQueue.offer(snapshot);
    }

    @Override
    public void onSystemMessage(String message) throws RemoteException {
        System.out.println("[RMI] SystemMessage: " + message);
    }
}
