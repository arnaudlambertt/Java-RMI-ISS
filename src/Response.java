import java.io.Serializable;

public class Response implements Serializable {
    private final Request req;
    private int result;
    private String statusCode;
    private double executionTime;
    private double waitingTime;
    private final int serverZone;

    public Response(Request req, int serverZone) {
        this.req = req;
        this.serverZone = serverZone;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setStatusCode(String statusCode) {
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

    public String getStatusCode() {
        return statusCode;
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public int getServerZone() {
        return serverZone;
    }

    @Override
    public String toString() {
        return "Response{" +
                "req=" + req +
                ", serverZone=" + serverZone +
                ", result=" + result +
                ", statusCode=" + statusCode +
                ", executionTime=" + executionTime +
                ", waitingTime=" + waitingTime +
                '}';
    }
}
