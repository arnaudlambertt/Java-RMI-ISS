import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerSimulator{
    public ServerSimulator() {
    }
    public static void main(String[] args) {
        try {
            int serverAmount = 5;
            ArrayList<Server> servers = new ArrayList<>();
            ArrayList<ServerInterface> stubs = new ArrayList<>();


            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();

            for(int i = 0; i < serverAmount; i++) {
                servers.add(new Server(i+1));
                stubs.add((ServerInterface) UnicastRemoteObject.exportObject(servers.get(i), 0));
                registry.bind("192.168.0.154:443" + i, stubs.get(i));
            }

            Proxy proxyServer = new Proxy();
            ProxyInterface proxyStub = (ProxyInterface) UnicastRemoteObject.exportObject(proxyServer, 0);
            registry.bind("example.com", proxyServer);


            System.err.println("Servers are ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

    }
}
