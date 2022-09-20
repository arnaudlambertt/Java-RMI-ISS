import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Proxy implements ProxyInterface {

    ArrayList<String> serverAddresses = new ArrayList<>();
    ArrayList<ServerInterface> stubs = new ArrayList<>();

    Proxy() throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(1099);
        serverAddresses.add("192.168.0.154:80");
        serverAddresses.add("245.208.163.94:80");
        serverAddresses.add("233.78.39.114:80");
        serverAddresses.add("178.67.237.95:80");
        serverAddresses.add("152.149.64.160:80");
        for(String serverAddress : serverAddresses) {
            stubs.add((ServerInterface) registry.lookup(serverAddress));
        }
    }

    public String requestNeighbor(int zone) throws RemoteException{
        int higher = zone%5;
        int lower = (zone+3)%5;
        int lowerQueue, higherQueue;
        String neighborAddress = serverAddresses.get(zone-1); // local server

        lowerQueue = stubs.get(lower).getQueueLength();
        higherQueue = stubs.get(higher).getQueueLength();

        if (lowerQueue < 8 || higherQueue < 8) {
            if (lowerQueue < higherQueue)
                neighborAddress = serverAddresses.get(lower);
            else
                neighborAddress = serverAddresses.get(higher);
        }

        return neighborAddress;
    }

    @Override
    public String requestConnection(int zone) throws RemoteException {
        return stubs.get(zone-1).getQueueLength() >= 20 ?  requestNeighbor(zone) : serverAddresses.get(zone-1);
    }
}
