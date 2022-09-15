import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerInterface stubA = (ServerInterface) registry.lookup("192.168.0.154:443");

            Request req = new Request("getA", new String[]{"22", "33"}, 0);
            Request req2 = new Request("getB", new String[]{"11", "44"}, 0);

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq2 = new MarshalledObject<>(req2);
                    System.out.println("Server response: " + stubA.queryRequest(mReq2).get().getReq().getMethodName());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    System.out.println("Server response: " + stubA.queryRequest(new MarshalledObject<Request>(req)).get().getReq().getMethodName());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
