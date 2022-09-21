import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request implements Serializable {
    private final String methodName;
    private final ArrayList<String> arguments;
    private final int clientZone;

    public Request(String methodName, List<String> arguments, int clientZone) {
        this.methodName = methodName;
        this.arguments = new ArrayList<String>(arguments);
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

    @Override
    public String toString() {
        return "Request{" +
                "methodName='" + methodName + '\'' +
                ", arguments=" + arguments +
                ", clientZone=" + clientZone +
                '}';
    }

}
