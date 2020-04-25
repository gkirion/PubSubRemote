package com.george.pubsub.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.http.HttpBuilder;
import pubsub.broker.Message;
import pubsub.broker.Receivable;

import java.io.IOException;

public class RemoteSubscriber implements Receivable {

    private RemoteAddress remoteAddress;
    private ObjectMapper mapper;

    public RemoteSubscriber() {
    }

    public RemoteSubscriber(RemoteAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public RemoteAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(RemoteAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void receive(Message message) {
        try {
            HttpBuilder.get(remoteAddress.getIp(), remoteAddress.getPort(), "receive")
                    .body(mapper.writeValueAsString(message)).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
