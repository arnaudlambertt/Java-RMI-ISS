import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    String getIp() throws RemoteException;
    String getPort() throws RemoteException;

    Response sendRequest(Request req);
}
