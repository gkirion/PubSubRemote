package com.george.pubsub.remote;

import java.util.Objects;

public class RemoteAddress {

    private String ip;
    private int port;

    public RemoteAddress() {}

    public RemoteAddress(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteAddress that = (RemoteAddress) o;
        return port == that.port &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "RemoteAddress{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }

}
