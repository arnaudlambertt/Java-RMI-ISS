import java.util.ArrayList;

public class Request {
    private final String methodName;
    private final ArrayList<String> arguments;
    private final boolean delayed; //OR ZONE

    public Request(String methodName, ArrayList<String> arguments, boolean delayed) {
        this.methodName = methodName;
        this.arguments = arguments;
        this.delayed = delayed;
    }
}
