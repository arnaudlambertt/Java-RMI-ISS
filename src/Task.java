import java.util.ArrayList;

public class Task {
    private final String methodName;
    private final ArrayList<String> arguments;
    private long waitingTime;
    private long executionTime;
    private int result;

    public Task(Request req) throws IllegalArgumentException, NoSuchMethodException {
        switch (req.getMethodName()){
            case "getPopulationOfCountry": {
                if (req.getArguments().size() < 1)
                    throw new IllegalArgumentException("Bad arguments: " + req.getArguments());
                break;
            }
            case "getNumberOfCities": {
                try {
                    if (req.getArguments().size() < 2)
                        throw new IllegalArgumentException("Bad arguments: " + req.getArguments());
                    Integer.valueOf(req.getArguments().get(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Bad arguments: " + req.getArguments());
                }
                break;
            }
            case "getNumberOfCountries": {
                try{
                    if(req.getArguments().size() < 2)
                        throw new IllegalArgumentException("Bad arguments: " + req.getArguments());

                    Integer.valueOf(req.getArguments().get(0));
                    Integer.valueOf(req.getArguments().get(1));

                    if(req.getArguments().size() > 2)
                        Integer.valueOf(req.getArguments().get(2));
                }catch(NumberFormatException e){
                    throw new IllegalArgumentException("Bad arguments: " + req.getArguments());
                }
                break;
            }
            default:
                throw new NoSuchMethodException("No such method: " + req.getMethodName());
        }

        this.methodName = req.getMethodName();
        this.arguments = req.getArguments();
        this.waitingTime = System.currentTimeMillis();
    }

    public void setWaitingTime(long waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMethodName() {
        return methodName;
    }

    public ArrayList<String> getArguments() {
        return arguments;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getResult() {
        return result;
    }
}
