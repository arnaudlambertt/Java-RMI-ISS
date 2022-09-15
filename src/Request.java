import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Request implements Serializable {
    private final String methodName;
    private final ArrayList<String> arguments;
    private final int clientZone;

    public Request(String methodName, String[] arguments, int clientZone) {
        this.methodName = methodName;
        this.arguments = new ArrayList<String>(Arrays.asList(arguments));
        this.clientZone = clientZone;
    }

    public String getMethodName() {
        return methodName;
    }
    public ArrayList<String> getArguments() {
        return arguments;
    }
    public int getClientZone() {
        return clientZone;
    }
}
