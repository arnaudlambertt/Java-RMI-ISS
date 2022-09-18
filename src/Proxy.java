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

    HashMap<Integer, String> serverMap = new HashMap<>();
    ArrayList<ServerInterface> stubArray = new ArrayList<>();

    Proxy() throws RemoteException, NotBoundException {
        serverMap.put(1, "192.168.0.154:443");
        /*
        serverMap.put(2, "192.168.0.155:443");
        serverMap.put(3, "192.168.0.156:443");
        serverMap.put(4, "192.168.0.157:443");
        serverMap.put(5, "192.168.0.158:443");
        */
        Registry registry = LocateRegistry.getRegistry(1099);
        for(Map.Entry<Integer, String> entry : serverMap.entrySet()){
            stubArray.add((ServerInterface) registry.lookup(entry.getValue()));
        }
    }

    public String requestNeighbor(int zone) throws RemoteException{
        int higher = zone%5;
        int lower = (zone+3)%5;

        //.getQueueLength() <= 8;
        if(!stubArray.get(higher).isBusy()){
            return serverMap.get(higher+1);
        }
        else if(!stubArray.get(lower).isBusy())
        {
            return serverMap.get(lower+1);
        }
        return serverMap.get(zone);

    }
    @Override
    public String requestConnection(int zone) throws RemoteException {
        return stubArray.get(zone-1).isBusy() ?  requestNeighbor(zone) : serverMap.get(zone);
    }
}
