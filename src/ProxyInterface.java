import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProxyInterface extends Remote {
    String requestConnection(int zone) throws RemoteException;
}
