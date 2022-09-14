import java.util.Queue;

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
            //..

            //interrupt if possible
        }
    }
}
