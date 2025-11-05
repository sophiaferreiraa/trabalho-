package gpjecc.blogspot.com.network;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class GameServerMain {
    
    // --- MUDANÇA DE PORTA ---
    private static final int RMI_PORT = 1100;

    public static GameServerImpl initServer() throws Exception {
        try {
            String ipLocal = InetAddress.getLocalHost().getHostAddress();
            
            if (ipLocal.equals("127.0.0.1") || ipLocal.equals("127.0.1.1")) {
                 ipLocal = "0.0.0.0"; 
                 System.out.println("Não foi possível detectar IP, ligando em 0.0.0.0");
            }
            
            String rmiHostName;
            try {
                rmiHostName = InetAddress.getLocalHost().getHostAddress();
                if (rmiHostName.equals("127.0.0.1")) rmiHostName = "0.0.0.0";
            } catch (Exception e) {
                rmiHostName = "0.0.0.0";
            }
            
            System.setProperty("java.rmi.server.hostname", rmiHostName);

            System.out.println("Iniciando servidor RMI na porta " + RMI_PORT + "...");
            
            // Cria o registro na nova porta
            LocateRegistry.createRegistry(RMI_PORT); 

            // --- CORREÇÃO DE PORTA AQUI ---
            // Passa a porta para o construtor do servidor
            GameServerImpl server = new GameServerImpl(RMI_PORT); 
            // -----------------------------

            // Monta a URL completa, incluindo a porta
            String bindUrl = "rmi://" + rmiHostName + ":" + RMI_PORT + "/GameServer";
            Naming.rebind(bindUrl, server);

            System.out.println("Servidor RMI pronto em: " + bindUrl);
            return server; // Retorna a instância
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Lança a exceção para o TartarugasJogo tratar
        }
    }
}