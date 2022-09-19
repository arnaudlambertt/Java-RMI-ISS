import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

    Integer getQueueLength() throws RemoteException;

    MarshalledObject<Response> queryRequest(MarshalledObject<Request> req) throws RemoteException;
}
