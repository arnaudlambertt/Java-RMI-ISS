import java.io.Serializable;

public class Response implements Serializable {
    private final Request req;

    private int result;

    private double execTime;

    private double waitTime;
    public Response(Request req) {
        this.req = req;
        this.result = 0;
        this.execTime = 0;
        this.waitTime = 0;
    }
    public void setResult(int result) {
        this.result = result;
    }

    public void setExecTime(double execTime) {
        this.execTime = execTime;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

    public int getResult() {
        return result;
    }

    public double getExecTime() {
        return execTime;
    }

    public double getWaitTime() {
        return waitTime;
    }

    public Request getReq() {
        return req;
    }
}
