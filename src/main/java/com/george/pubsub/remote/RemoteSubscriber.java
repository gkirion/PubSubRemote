package com.george.pubsub.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.http.HttpBuilder;
import pubsub.broker.Message;
import pubsub.broker.Receivable;

import java.io.IOException;
import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteSubscriber that = (RemoteSubscriber) o;
        return Objects.equals(remoteAddress, that.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteAddress);
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
