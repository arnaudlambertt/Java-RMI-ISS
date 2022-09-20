import java.io.Serializable;

public class Response implements Serializable {
    private final Request req;
    private int result;
    private int statusCode;
    private double executionTime;
    private double waitingTime;

    public Response(Request req) {
        this.req = req;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public Request getReq() {
        return req;
    }

    public int getResult() {
        return result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    @Override
    public String toString() {
        return "Response{" +
                "req=" + req +
                ", result=" + result +
                ", statusCode=" + statusCode +
                ", executionTime=" + executionTime +
                ", waitingTime=" + waitingTime +
                '}';
    }
}
