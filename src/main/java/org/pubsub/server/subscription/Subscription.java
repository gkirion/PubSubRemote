package org.pubsub.server.subscription;

import org.pubsub.server.subscriber.RemoteSubscriber;

public class Subscription {

    private String topic;
    private RemoteSubscriber subscriber;

    public Subscription() {}

    public Subscription(String topic, RemoteSubscriber subscriber) {
        this.topic = topic;
        this.subscriber = subscriber;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public RemoteSubscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(RemoteSubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "topic='" + topic + '\'' +
                ", subscriber=" + subscriber +
                '}';
    }

}
