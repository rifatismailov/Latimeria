package org.example.web;

public class Result {

    HostInfo hostInfo;
    String status;
    String tip;

    public Result(HostInfo hostInfo, String status, String tip) {
        this.hostInfo = hostInfo;
        this.status = status;
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "Result{" +
                "hostName='" + hostInfo.getHostName() + '\'' +
                ", hostAddress='" + hostInfo.getHostAddress() + '\'' +
                ", macAddress='" + hostInfo.getMacAddress() + '\'' +
                ", remoteAddr='" + hostInfo.getRemoteAddr() + '\'' +
                ", urlFile='" + hostInfo.getUrlFile() + '\'' +
                ", status='" + status + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
