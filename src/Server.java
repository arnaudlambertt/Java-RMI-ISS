import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements ServerInterface {
    private final String ip;
    private final String port;
    private final int zone;
    private final BlockingQueue<Task> waitingList;

    private final ArrayList<ArrayList<String>> dataset;
    private final Thread processingThread;

    public Server(String ip, String port, int zone){
        this.ip = ip;
        this.port = port;
        this.zone = zone;
        this.dataset = parseDataset("data/dataset.csv");
        this.waitingList = new LinkedBlockingQueue<>();
        this.processingThread = new Thread(this::processTasks);
        this.processingThread.start();
    }

    public ArrayList<ArrayList<String>> parseDataset(String fileName)
    {
        File file= new File(fileName);
        ArrayList<ArrayList<String>> parsedDataset = new ArrayList<>();
        Scanner inputStream;
        try{
            inputStream = new Scanner(file, "UTF-8");
            inputStream.nextLine();

            while(inputStream.hasNextLine()){
                String line= inputStream.nextLine();
                ArrayList<String> values = new ArrayList<>(Arrays.asList(line.split(";")));
                // this adds the currently parsed line to the 2-dimensional string array
                parsedDataset.add(values);
            }

            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // the following code lets you iterate through the 2-dimensional array
        /*int lineNo = 1;
        for(ArrayList<String> line: parsedDataset) {
            System.out.println(line.toString());
            lineNo++;
        }
        System.out.println(parsedDataset.size());
        */

        return parsedDataset;
    }

    public void processTasks(){
        while(true){
            try {
                Task currentTask = waitingList.take();
                long startExecutionTime = System.currentTimeMillis();
                currentTask.setWaitingTime(startExecutionTime-currentTask.getWaitingTime());

                switch (currentTask.getMethodName()){
                    case "getPopulationOfCountry": {
                        String arg0 = currentTask.getArguments().get(0);

                        currentTask.setResult(getPopulationOfCountry(arg0));
                        break;
                    }
                    case "getNumberOfCities": {
                        String arg0 = currentTask.getArguments().get(0);
                        String arg1 = currentTask.getArguments().get(1);

                        currentTask.setResult(getNumberOfCities(arg0,arg1));
                        break;
                    }
                    case "getNumberOfCountries": {
                        String arg0 = currentTask.getArguments().get(0);
                        String arg1 = currentTask.getArguments().get(1);

                        if(currentTask.getArguments().size() < 3)
                            currentTask.setResult(getNumberOfCountries(arg0,arg1));
                        else {
                            String arg2 = currentTask.getArguments().get(2);
                            currentTask.setResult(getNumberOfCountries(arg0,arg1,arg2));
                        }
                        break;
                    }

                    default:
                        break;
                }

                currentTask.setExecutionTime(System.currentTimeMillis()-startExecutionTime);
                synchronized (currentTask){
                    currentTask.notify();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public int getPopulationOfCountry(String countryCode) {
        int counter = 0;

        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode))
                counter += Integer.parseInt(line.get(4));
        }
        return counter;
    }

    public int getNumberOfCities(String countryCode, String minPopulation) {
        int counter = 0;
        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode) && Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation))
                counter += Integer.parseInt(line.get(4));
        }
        return counter;
    }

    public int getNumberOfCountries(String cityCount, String minPopulation) {

        int counter = 0;
        //      CountryCode, NumOfCitiesWithMinPop
        HashMap<String, Integer> countryCountMap = new HashMap<>();

        for(ArrayList<String> line : dataset) {
            if(Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation)){
                countryCountMap.put(line.get(2), (countryCountMap.containsKey(line.get(2)) ? countryCountMap.get(line.get(2)) : 0) + 1);
            }
        }
        for(Map.Entry<String, Integer> countryCount : countryCountMap.entrySet()){
            if(countryCount.getValue() >= Integer.parseInt(cityCount)){
                counter++;
            }
        }
        return counter;
    }

    public int getNumberOfCountries(String cityCount, String minPopulation, String maxPopulation) {

        int counter = 0;
        //      CountryCode, NumOfCitiesWithMinPop
        HashMap<String, Integer> countryCountMap = new HashMap<>();

        for(ArrayList<String> line : dataset) {
            if(Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation) && Integer.parseInt(line.get(4)) <= Integer.parseInt(maxPopulation)){
                countryCountMap.put(line.get(2), (countryCountMap.containsKey(line.get(2)) ? countryCountMap.get(line.get(2)) : 0) + 1);
            }
        }
        for(Map.Entry<String, Integer> countryCount : countryCountMap.entrySet()){
            if(countryCount.getValue() >= Integer.parseInt(cityCount)){
                counter++;
            }
        }
        return counter;
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
        try {
            Response res = new Response(req.get());
            Thread.sleep(req.get().getClientZone() == this.getZone() ? 80 : 170);

            try {
                Task task = new Task(req.get());
                waitingList.put(task);

                synchronized (task){
                    task.wait();
                }

                res.setResult(task.getResult());
                res.setWaitingTime(task.getWaitingTime());
                res.setExecutionTime(task.getExecutionTime());
                res.setStatusCode(200);

            } catch (IllegalArgumentException | NoSuchMethodException e){
                res.setStatusCode(400);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return new MarshalledObject<>(res);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
