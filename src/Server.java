import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements ServerInterface {
    private String name;

    public Server(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String sayHello() throws RemoteException {
        return "Hello from Server " + getName();
    }
}
