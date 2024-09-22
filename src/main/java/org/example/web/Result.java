package org.example.web;

public class Result {
    String hostInfo;
    String status;
    String tip;

    public Result(String hostInfo, String status, String tip) {
        this.hostInfo = hostInfo;
        this.status = status;
        this.tip = tip;
    }

    public String getHostInfo() {
        return hostInfo;
    }

    public void setHostInfo(String hostInfo) {
        this.hostInfo = hostInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getTip() {
        return tip;
    }
    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "Result{" +
                "hostInfo='" + hostInfo + '\'' +
                ", status='" + status + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
