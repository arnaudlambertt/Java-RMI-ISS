import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerSimulator{
    public ServerSimulator() {
    }
    public static void main(String[] args) {
        try {
            // Bind the remote object's stub in the registry
            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();

            Server serverA = new Server("192.168.0.1", "443", 1);
            ServerInterface stubA = (ServerInterface) UnicastRemoteObject.exportObject(serverA, 0);
            registry.bind("192.168.0.154:443", stubA);

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
