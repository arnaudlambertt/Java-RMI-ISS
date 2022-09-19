import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.util.*;

public class Server implements ServerInterface {
    private final int zone;
    private final Queue<Task> waitingList;

    private final ArrayList<ArrayList<String>> dataset;

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


    public Server(int zone){
        this.zone = zone;
        this.dataset = parseDataset("data/dataset.csv");
        this.waitingList = new LinkedList<>();
        this.processingThread = new ProcessingThread(waitingList);
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

    public int getPopulationOfCountry(String countryCode) {
        int counter = 0;

        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode))
                counter += Integer.parseInt(line.get(4));
        }
        return counter;
    }

    public int getNumberOfCities(String countryCode, String min) {
        int counter = 0;
        for (ArrayList<String> line : dataset) {
            if (line.get(2).equals(countryCode) && Integer.parseInt(line.get(4)) > Integer.parseInt(min))
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
    public Integer getQueueLength() throws RemoteException {
        return waitingList.size();
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
            //System.out.println(getNumberOfCountries("30", "100000", "800000"));
            res.setResult(getNumberOfCountries("30", "100000", "800000"));
            return new MarshalledObject<>(res);
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
