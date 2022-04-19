package org.pubsub.server.subscription;

import org.pubsub.server.subscriber.RemoteSubscriber;

public class SubscriptionBuilder {

    private String topic;
    private RemoteSubscriber subscriber;

    public SubscriptionBuilder() {}

    public SubscriptionBuilder topic(String topic) {
        this.topic = topic;
        return this;
    }

    public SubscriptionBuilder subscriber(RemoteSubscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public Subscription build() {
        return new Subscription(topic, subscriber);
    }

}
