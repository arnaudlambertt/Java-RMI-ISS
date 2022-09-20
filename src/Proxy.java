import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;


public class Proxy implements ProxyInterface {

    private final ArrayList<String> serverAddresses;
    private final ArrayList<ServerInterface> stubs;
    private final ArrayList<Integer> storedQueueLengths;
    private final AtomicIntegerArray requestCounter;

    Proxy() throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(1099);
        this.serverAddresses = new ArrayList<>();
        serverAddresses.add("192.168.0.154:80");
        serverAddresses.add("245.208.163.94:80");
        serverAddresses.add("233.78.39.114:80");
        serverAddresses.add("178.67.237.95:80");
        serverAddresses.add("152.149.64.160:80");
        this.stubs = new ArrayList<>();

        this.storedQueueLengths = new ArrayList<>(Collections.nCopies(5, 0));
        this.requestCounter = new AtomicIntegerArray(serverAddresses.size());

        for(String serverAddress : serverAddresses) {
            stubs.add((ServerInterface) registry.lookup(serverAddress));
        }

        for(int i = 0; i < serverAddresses.size(); i++)
            updateServerInfo(i+1);
    }

    public String requestNeighbor(int zone) throws RemoteException{
        int higher = zone%5;
        int lower = (zone+3)%5;
        int lowerQueue, higherQueue;
        String neighborAddress = serverAddresses.get(zone-1); // local server

        lowerQueue = storedQueueLengths.get(lower);
        higherQueue = storedQueueLengths.get(higher);

        if (lowerQueue < 8 || higherQueue < 8) {
            if (lowerQueue < higherQueue)
                neighborAddress = serverAddresses.get(lower);
            else
                neighborAddress = serverAddresses.get(higher);
        }

        return neighborAddress;
    }

    public Runnable updateServerInfo(int zone) {
        try {
            storedQueueLengths.set(zone-1, stubs.get(zone-1).getQueueLength());
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    @Override
    public String requestConnection(int zone) throws RemoteException {
        if(requestCounter.incrementAndGet(zone-1) == 20) {
            requestCounter.set(zone-1,0);
            Thread updatingThread = new Thread(updateServerInfo(zone));
            updatingThread.start();
        }

        return stubs.get(zone-1).getQueueLength() >= 20 ?  requestNeighbor(zone) : serverAddresses.get(zone-1);
    }
}
