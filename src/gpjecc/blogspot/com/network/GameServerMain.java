package gpjecc.blogspot.com.network;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GameServerMain {
    
    // MODIFICAÇÃO: Agora retorna o GameServerImpl
    public static GameServerImpl initServer() throws Exception {
        try {
            String ipLocal = InetAddress.getLocalHost().getHostAddress();
            
            if (ipLocal.equals("127.0.0.1") || ipLocal.equals("127.0.1.1")) {
                 ipLocal = "0.0.0.0"; 
                 System.out.println("Não foi possível detectar IP, ligando em 0.0.0.0");
            }
            
            // Tenta usar o IP real para RMI, se falhar, usa 0.0.0.0
            String rmiHostName;
            try {
                rmiHostName = InetAddress.getLocalHost().getHostAddress();
                if (rmiHostName.equals("127.0.0.1")) rmiHostName = "0.0.0.0";
            } catch (Exception e) {
                rmiHostName = "0.0.0.0";
            }
            
            System.setProperty("java.rmi.server.hostname", rmiHostName);

            System.out.println("Iniciando servidor RMI...");
            LocateRegistry.createRegistry(1099);

            GameServerImpl server = new GameServerImpl();
            Naming.rebind("GameServer", server);

            System.out.println("Servidor RMI pronto em: rmi://" + rmiHostName + "/GameServer");
            return server; // Retorna a instância
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Lança a exceção para o TartarugasJogo tratar
        }
    }
}