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
    ArrayList<ServerInterface> stubs = new ArrayList<>();

    Proxy() throws RemoteException, NotBoundException {

        Registry registry = LocateRegistry.getRegistry(1099);
        for(int i = 1; i <= 5; i++){
            String ip = "192.168.0.154:443" + (i - 1);
            serverMap.put(i, ip);
            stubs.add((ServerInterface) registry.lookup(ip));
        }
    }

    public String requestNeighbor(int zone) throws RemoteException{
        int higher = zone%5;
        int lower = (zone+3)%5;

        //.getQueueLength() <= 8;
        if(stubs.get(higher).getQueueLength() <= 8){
            return serverMap.get(higher+1);
        }
        else if(stubs.get(lower).getQueueLength() <= 8)
        {
            return serverMap.get(lower+1);
        }
        return serverMap.get(zone);

    }
    @Override
    public String requestConnection(int zone) throws RemoteException {
        return stubs.get(zone-1).getQueueLength() <= 20 ?  requestNeighbor(zone) : serverMap.get(zone);
    }
}
