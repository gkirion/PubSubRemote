package org.pubsub.model;

import org.springframework.web.client.RestTemplate;

public class RemoteSubscriberBuilder {

    private String address;
    private int port;
    private RestTemplate restTemplate;

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

    public RemoteSubscriberBuilder restTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        return this;
    }

    public RemoteSubscriber build() {
        return new RemoteSubscriber(address, port, restTemplate);
    }

}
