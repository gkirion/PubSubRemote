package org.pubsub.server.subscriber;

public class RemoteSubscriberBuilder {

    private String address;
    private int port;

    public RemoteSubscriberBuilder() {

    }

    public RemoteSubscriberBuilder address(String address) {
        this.address = address;
        return this;
    }

    public RemoteSubscriberBuilder port(int port) {
        this.port = port;
        return this;
    }

    public RemoteSubscriber build() {
        return new RemoteSubscriber(address, port);
    }

}
