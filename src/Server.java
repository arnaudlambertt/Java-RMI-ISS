import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
public class Server implements ServerInterface {
    private final String ip;
    private final String port;

    private final int zone;

    private final Queue<Task> waitingList;

    private final Thread processingThread;
    public class ProcessingThread extends Thread {
        private final Queue<Task> waitingList; //task ?

        public ProcessingThread(Queue<Task> waitingList) {
            this.waitingList = waitingList;
        }

        @Override
        public void run() {
            while (true){
                Task currentTask = waitingList.remove();
                //process the next request in the queue
                //...

                //interrupt if possible
            }
        }
    }

    public Server(String ip, String port, int zone){
        this.ip = ip;
        this.port = port;
        this.zone = zone;
        this.waitingList = new LinkedList<>();
        this.processingThread = new ProcessingThread(waitingList);
    }

    @Override
    public String getIp() throws RemoteException{
        return ip;
    }

    @Override
    public String getPort() throws RemoteException{
        return port;
    }

    public int getZone() {
        return zone;
    }

    @Override
    public MarshalledObject<Response> queryRequest(MarshalledObject<Request> req) throws RemoteException{
        Response res;
        try {
            res = new Response(req.get());
            Thread.sleep(req.get().getClientZone() == this.getZone() ? 80 : 170);
            //add to queue/waiting list
            return new MarshalledObject<>(res);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
