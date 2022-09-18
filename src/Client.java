import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ProxyInterface proxyStub = (ProxyInterface) registry.lookup("example.com");

            String serverIP = proxyStub.requestConnection(1);
            ServerInterface serverStub = (ServerInterface) registry.lookup(serverIP);

            Request req = new Request("getA", new String[]{"22", "33"}, 0);
            Request req2 = new Request("getB", new String[]{"11", "44"}, 0);

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq2 = new MarshalledObject<>(req2);
                    System.out.println("Server response: " + serverStub.queryRequest(mReq2).get().getResult());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    System.out.println("Server response: " + serverStub.queryRequest(new MarshalledObject<Request>(req)).get().getResult());
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
