import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            ServerInterface stubA = (ServerInterface) registry.lookup("ServerA");
            ServerInterface stubB = (ServerInterface) registry.lookup("ServerB");

            String responseA = stubA.sayHello();
            String responseB = stubB.sayHello();

            System.out.println("response: " + responseA);
            System.out.println("response: " + responseB);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
