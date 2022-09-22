import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements ServerInterface {
    private final int zone;
    private final BlockingQueue<Task> waitingQueue;
    private final ArrayList<ArrayList<String>> dataset;
    private final CachingMap<String,Integer> cache;
    private final int cacheEnabled;

    /**
     * Server constructor
     * @param zone integer
     * @param enableCache boolean
     */
    public Server(int zone, int enableCache){
        this.zone = zone;
        this.dataset = parseDataset("data/dataset.csv");
        this.waitingQueue = new LinkedBlockingQueue<>();
        this.cache = new CachingMap<>(200);
        this.cacheEnabled = enableCache;
        Thread processingThread = new Thread(this::processTasks);
        processingThread.start();
    }

    /**
     * Parses the dataset file
     * @param fileName Path to file to parse
     * @return Two-dimensional array of Strings
     */
    public ArrayList<ArrayList<String>> parseDataset(String fileName)
    {
        File file = new File(fileName);
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

        return parsedDataset;
    }

    /**
     * Waits and takes the first task element in the waiting queue and processes it, before notifying the RMI thread
     */
    public void processTasks(){
        while(true){
            try {
                Task currentTask = waitingQueue.take();
                long startExecutionTime = System.currentTimeMillis();
                currentTask.setWaitingTime(startExecutionTime-currentTask.getWaitingTime());

                String queryString = currentTask.getMethodName() + currentTask.getArguments();

                Integer cacheResult = null;

                if(cacheEnabled == 1)
                    cacheResult = cache.get(queryString);

                if(cacheResult == null){

                    int result;

                    switch (currentTask.getMethodName()){
                        case "getPopulationofCountry": {
                            String arg0 = currentTask.getArguments().get(0);
                            result = getPopulationOfCountry(arg0);
                            break;
                        }
                        case "getNumberofCities": {
                            String arg0 = currentTask.getArguments().get(0);
                            String arg1 = currentTask.getArguments().get(1);
                            result = getNumberOfCities(arg0,arg1);
                            break;
                        }
                        case "getNumberofCountries": {
                            String arg0 = currentTask.getArguments().get(0);
                            String arg1 = currentTask.getArguments().get(1);
                            if(currentTask.getArguments().size() < 3)
                                result = getNumberOfCountries(arg0,arg1);
                            else {
                                String arg2 = currentTask.getArguments().get(2);
                                result = getNumberOfCountries(arg0,arg1,arg2);
                            }
                            break;
                        }
                        default:
                            result = 0;
                            break;
                    }
                    currentTask.setResult(result);

                    if(cacheEnabled == 1)
                        cache.put(queryString, result);

                }else{
                    currentTask.setResult(cacheResult);
                }

                currentTask.setExecutionTime(System.currentTimeMillis()-startExecutionTime);

                synchronized (currentTask) {
                    currentTask.notify();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Counts the population for a given countryCode
     * @param countryCode string
     * @return result
     */
    public int getPopulationOfCountry(String countryCode) {
        int counter = 0;

        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode))
                counter += Integer.parseInt(line.get(4));
        }
        return counter;
    }

    /**
     * Counts the number of cities with the at least minimum population for a given countryCode
     * @param countryCode string
     * @param minPopulation string
     * @return result
     */
    public int getNumberOfCities(String countryCode, String minPopulation) {
        int counter = 0;
        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode) && Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation))
                counter++;
        }
        return counter;
    }

    /**
     * Counts the number of countries with at least cityCount of cities with at least the minimum population
     * @param cityCount string
     * @param minPopulation string
     * @return result
     */
    public int getNumberOfCountries(String cityCount, String minPopulation) {

        int counter = 0;
        //      CountryCode, NumOfCitiesWithMinPop
        HashMap<String, Integer> countryCountMap = new HashMap<>();

        for(ArrayList<String> line : dataset) {
            if(Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation)){
                countryCountMap.put(line.get(2), (countryCountMap.getOrDefault(line.get(2), 0)) + 1);
            }
        }
        for(Map.Entry<String, Integer> countryCount : countryCountMap.entrySet()){
            if(countryCount.getValue() >= Integer.parseInt(cityCount)){
                counter++;
            }
        }
        return counter;
    }

    /**
     * Counts the number of countries with at least cityCount of cities with population between min and max
     * @param cityCount string
     * @param minPopulation string
     * @param maxPopulation string
     * @return result
     */
    public int getNumberOfCountries(String cityCount, String minPopulation, String maxPopulation) {

        int counter = 0;
        //      CountryCode, NumOfCitiesWithMinPop
        HashMap<String, Integer> countryCountMap = new HashMap<>();

        for(ArrayList<String> line : dataset) {
            if(Integer.parseInt(line.get(4)) >= Integer.parseInt(minPopulation) && Integer.parseInt(line.get(4)) <= Integer.parseInt(maxPopulation)){
                countryCountMap.put(line.get(2), (countryCountMap.getOrDefault(line.get(2), 0)) + 1);
            }
        }
        for(Map.Entry<String, Integer> countryCount : countryCountMap.entrySet()){
            if(countryCount.getValue() >= Integer.parseInt(cityCount)){
                counter++;
            }
        }
        return counter;
    }

    /**
     * RMI method that returns the size of the waitingQueue
     * @return integer
     * @throws RemoteException RMI issue
     */
    @Override
    public Integer getQueueLength() throws RemoteException {
        return waitingQueue.size();
    }

    public int getZone() {
        return zone;
    }

    /**
     * RMI method that accepts a request, creates a task, waits for it to be processed, then returns the response to the client
     * @param req Marshalled request
     * @return Marshalled response object containing the result and more
     * @throws RemoteException RMI issue
     */
    @Override
    public MarshalledObject<Response> queryRequest(MarshalledObject<Request> req) throws RemoteException{
        try {
            Response res = new Response(req.get(), this.getZone());
            Thread.sleep(req.get().getClientZone() == this.getZone() ? 80 : 170);

            try {
                Task task = new Task(req.get());

                synchronized (task){
                    waitingQueue.put(task);
                    task.wait();
                }

                res.setResult(task.getResult());
                res.setWaitingTime(task.getWaitingTime());
                res.setExecutionTime(task.getExecutionTime());
                res.setStatusCode("200");

            } catch (IllegalArgumentException | NoSuchMethodException e){
                res.setStatusCode("400");
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
