import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client {

    public static ArrayList<Request> parseInputFile(String fileName){
        File file = new File(fileName);
        ArrayList<Request> requests = new ArrayList<>();
        Scanner inputStream;
        try{
            inputStream = new Scanner(file, "UTF-8");

            while(inputStream.hasNextLine()){
                String line = inputStream.nextLine();
                ArrayList<String> values = new ArrayList<>();

                Scanner lineScanner = new Scanner(line);
                while(lineScanner.hasNext())
                    values.add(lineScanner.next());

                requests.add(new Request(values.get(0),values.subList(1,values.size()-1),values.get(values.size()-1).charAt(5)-'0'));
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return requests;
    }

    public static void main(String[] args) {
        try {
            int clientAmount = 50;

            Registry registry = LocateRegistry.getRegistry(null);
            ProxyInterface proxyStub = (ProxyInterface) registry.lookup("example.com");

            ArrayList<Request> requests = parseInputFile("data/input.txt");
            System.out.println(requests.size());
            //Request req = new Request("getA", new String[]{"22", "33"}, 5);
            //Request req2 = new Request("getNumberOfCities", new String[]{"IT", "ABC"}, 5);
            //Request req3 = new Request("getNumberOfCities", new String[]{"IT", "100000"}, 5);
            //Request req4 = new Request("getNumberOfCountries", Arr"10", "100000"}, 5);
            //Request req5 = new Request("getNumberOfCountries", new String[]{"20", "100000", "10000000"}, 5);



//            new Thread(() -> {
//                try {
//                    String serverIP = proxyStub.requestConnection(5);
//                    ServerInterface serverStub = (ServerInterface) registry.lookup(serverIP);
//                    MarshalledObject<Request> mReq5 = new MarshalledObject<>(req5);
//                    System.out.println(serverStub.queryRequest(mReq5).get());
//                } catch (IOException | ClassNotFoundException | NotBoundException e) {
//                    throw new RuntimeException(e);
//                }
//            }).start();

        } catch (RemoteException | NotBoundException e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
