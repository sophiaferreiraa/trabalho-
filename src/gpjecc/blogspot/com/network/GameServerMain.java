package gpjecc.blogspot.com.network;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GameServerMain {
    public static void initServer() {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            System.out.println("Iniciando servidor RMI...");
            LocateRegistry.createRegistry(1099);

            GameServerImpl server = new GameServerImpl();
            Naming.rebind("GameServer", server);

            System.out.println("Servidor RMI pronto em: rmi://127.0.0.1/GameServer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
