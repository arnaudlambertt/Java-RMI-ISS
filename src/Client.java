import java.io.*;
import java.lang.reflect.Array;
import java.rmi.MarshalledObject;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

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
            int clientThreads = 50;
            SynchronizedCachingMap<String,Integer> cache = new SynchronizedCachingMap<>(50);
            Integer cachingMode = Integer.parseInt(args[0]);

            Registry registry = LocateRegistry.getRegistry(null);
            ProxyInterface proxyStub = (ProxyInterface) registry.lookup("example.com");

            ArrayList<Request> requests = parseInputFile("data/input.txt");

            String fileName;
            switch(cachingMode){
                case 0:{
                    fileName = "naive_server.txt";
                    break;
                }
                case 1:{
                    fileName = "server_cache.txt";
                    break;
                }
                case 2:{
                    fileName = "client_cache.txt";
                    break;
                }
                default:
                    throw new IllegalArgumentException("Illegal Argument: Only 0, 1 and 2 are valid.");
            }

            ArrayList<Thread> threads = new ArrayList<>(clientThreads);

            AtomicIntegerArray[] averageTimings = new AtomicIntegerArray[4];
            for(int i = 0; i < 4; i++)
                averageTimings[i] = new AtomicIntegerArray(4);

            FileWriter file = new FileWriter("data/" + fileName);
            BufferedWriter outputStream = new BufferedWriter(file);

            for(int i = 0; i < clientThreads; i++){
                int finalI = i;

                threads.add(new Thread(() -> {
                    for(int j = finalI*requests.size()/clientThreads; j < (finalI + 1)*requests.size()/clientThreads; j++){
                        try {
                            Integer cacheResult = null;
                            Request req = requests.get(j);
                            Response res = null;
                            String queryString = req.getMethodName() + req.getArguments();
                            long turnaroundTime = System.currentTimeMillis();

                            if(cachingMode == 2)
                                cacheResult = cache.get(queryString);

                            if(cacheResult == null){
                                String serverIP = proxyStub.requestConnection(req.getClientZone());
                                ServerInterface serverStub = (ServerInterface) registry.lookup(serverIP);
                                MarshalledObject<Request> mReq = new MarshalledObject<>(req);
                                res = serverStub.queryRequest(mReq).get();

                                if(cachingMode == 2)
                                    cache.put(queryString, res.getResult());
                            }
                            else {
                                res = new Response(req ,0);
                                res.setResult(cacheResult);
                                res.setStatusCode("200 (cache)");
                            }
                            turnaroundTime = System.currentTimeMillis() - turnaroundTime;
                            int methodIndex;
                            switch(req.getMethodName()){
                                case "getPopulationofCountry":
                                    methodIndex = 0;
                                    break;
                                case "getNumberofCities":
                                    methodIndex = 1;
                                    break;
                                case "getNumberofCountries": {
                                    if(req.getArguments().size() < 3)
                                        methodIndex = 2;
                                    else
                                        methodIndex = 3;
                                    break;
                                }
                                default:
                                    throw new NoSuchMethodException("The method does not exist.");
                            }
                            averageTimings[methodIndex].addAndGet(0, (int)turnaroundTime);
                            averageTimings[methodIndex].addAndGet(1, (int)res.getExecutionTime());
                            averageTimings[methodIndex].addAndGet(2, (int)res.getWaitingTime());
                            averageTimings[methodIndex].incrementAndGet(3);


                            outputStream.write(res.getResult() + " " + queryString + " (turnaround time: " + turnaroundTime + " ms, execution time: "
                                    + res.getExecutionTime() + " ms, waiting time: " + res.getWaitingTime() + " ms, processed by Server " + res.getServerZone() + ")\n");


                            //System.out.println("Client No." + (finalI+1) + " Request No." + j + " " + res + " turnaroundTime: " + turnaroundTime);

                        } catch (IOException | ClassNotFoundException | NotBoundException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    try {
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
                threads.get(i).start();
            }
            for(Thread thread: threads)
                thread.join();

            String methodName;
            for(int i = 0; i < 4; i++){
                switch(i) {
                    case 0:
                        methodName = "getPopulationofCountry";
                        break;
                    case 1:
                        methodName = "getNumberofCities";
                        break;
                    default:
                        methodName = "getNumberofCountries";

                    }
                    outputStream.write(methodName + " turnaround time: " + averageTimings[i].get(0)/averageTimings[i].get(3)
                            + " ms, execution time: " + averageTimings[i].get(1)/averageTimings[i].get(3)
                            + " ms, waiting time: " + averageTimings[i].get(2)/averageTimings[i].get(3) + " ms\n");
                }
            outputStream.flush();
            //outputStream.close();

        } catch (NotBoundException | IOException | InterruptedException e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
