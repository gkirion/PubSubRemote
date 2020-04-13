package com.george.pubsub.remote;

public class RemoteSubscription {

    private String topic;
    private RemoteAddress remoteSubscriber;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public RemoteAddress getRemoteSubscriber() {
        return remoteSubscriber;
    }

    public void setRemoteSubscriber(RemoteAddress remoteSubscriber) {
        this.remoteSubscriber = remoteSubscriber;
    }

    @Override
    public String toString() {
        return "RemoteSubscription{" +
                "topic='" + topic + '\'' +
                ", remoteSubscriber=" + remoteSubscriber +
                '}';
    }

}
