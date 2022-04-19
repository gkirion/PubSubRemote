package org.pubsub.server.subscriber;

import org.pubsub.core.message.Message;
import org.pubsub.core.subscriber.Subscriber;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

public class RemoteSubscriber implements Subscriber {

    private String address;
    private int port;
    private RestTemplate restTemplate = new RestTemplate();

    public RemoteSubscriber() {

    }

    public RemoteSubscriber(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void receive(Message message) {
        restTemplate.exchange(
                RequestEntity.post(URI.create("http://" + address + ":" + port + "/receive"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(message),
                Message.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteSubscriber that = (RemoteSubscriber) o;
        return port == that.port &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public String toString() {
        return "RemoteSubscriber{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

}
