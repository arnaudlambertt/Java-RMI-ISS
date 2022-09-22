import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerSimulator{

    /**
     * ServerSimulator that creates a proxy and 5 servers
     * @param args 0: Server cache disabled, 1: Server cache enabled
     */
    public static void main(String[] args) {

        int cachingMode = Integer.parseInt(args[0]);

        if(cachingMode != 0 && cachingMode != 1)
            throw new IllegalArgumentException("Illegal Argument: Only 0 and 1 are valid");

        try {
            int serverAmount = 5;
            ArrayList<ServerInterface> stubs = new ArrayList<>();

            LocateRegistry.createRegistry(1099);
            Registry registry = LocateRegistry.getRegistry();

            for(int i = 0; i < serverAmount; i++) {
                Server server = new Server(i+1, cachingMode);
                stubs.add((ServerInterface) UnicastRemoteObject.exportObject(server, 0));
            }
            registry.bind("192.168.0.154:80" , stubs.get(0)); //Zone 1
            registry.bind("245.208.163.94:80", stubs.get(1)); //Zone 2
            registry.bind("233.78.39.114:80" , stubs.get(2)); //Zone 3
            registry.bind("178.67.237.95:80" , stubs.get(3)); //Zone 4
            registry.bind("152.149.64.160:80", stubs.get(4)); //Zone 5

            Proxy proxyServer = new Proxy();
            ProxyInterface proxyStub = (ProxyInterface) UnicastRemoteObject.exportObject(proxyServer, 0);
            registry.bind("example.com", proxyStub);

            System.err.println("Servers are ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e);
            e.printStackTrace();
        }

    }
}
