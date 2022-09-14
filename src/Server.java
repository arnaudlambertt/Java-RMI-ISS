import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
public class Server implements ServerInterface {
    private final String ip;
    private final String port;

    //waiting list
    private final Queue<Task> waitingList; //task ?
    
    private final Thread processingThread;

    public Server(String ip, String port){
        this.ip = ip;
        this.port = port;
        this.waitingList = new LinkedList<Task>();
        processingThread = new ProcessingThread(waitingList);
    }

    @Override
    public String getIp() throws RemoteException{
        return ip;
    }

    @Override
    public String getPort() throws RemoteException{
        return port;
    }

}
