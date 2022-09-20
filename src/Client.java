import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            int clientAmount = 50;

            Registry registry = LocateRegistry.getRegistry(host);
            ProxyInterface proxyStub = (ProxyInterface) registry.lookup("example.com");

            String serverIP = proxyStub.requestConnection(5);
            ServerInterface serverStub = (ServerInterface) registry.lookup(serverIP);

            Request req = new Request("getA", new String[]{"22", "33"}, 5);
            Request req2 = new Request("getNumberOfCities", new String[]{"IT", "ABC"}, 5);
            Request req3 = new Request("getNumberOfCities", new String[]{"IT", "100000"}, 5);
            Request req4 = new Request("getNumberOfCountries", new String[]{"10", "100000"}, 5);
            Request req5 = new Request("getNumberOfCountries", new String[]{"20", "100000", "10000000"}, 5);

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq = new MarshalledObject<>(req);
                    System.out.println(serverStub.queryRequest(mReq).get());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq2 = new MarshalledObject<>(req2);
                    System.out.println(serverStub.queryRequest(mReq2).get());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq3 = new MarshalledObject<>(req3);
                    System.out.println(serverStub.queryRequest(mReq3).get());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq4 = new MarshalledObject<>(req4);
                    System.out.println(serverStub.queryRequest(mReq4).get());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            new Thread(() -> {
                try {
                    MarshalledObject<Request> mReq5 = new MarshalledObject<>(req5);
                    System.out.println(serverStub.queryRequest(mReq5).get());
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
