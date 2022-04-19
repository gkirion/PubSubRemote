package org.pubsub.server.web;


import org.pubsub.core.broker.exception.AlreadySubscribedException;
import org.pubsub.core.broker.exception.SubscriberNotExistsException;
import org.pubsub.core.broker.exception.TopicNotExistsException;
import org.pubsub.core.broker.service.MessageBroker;
import org.pubsub.core.message.Message;
import org.pubsub.server.subscription.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MessageBrokerEndpoint {

    @Autowired
    private MessageBroker messageBroker;

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) throws AlreadySubscribedException {
        messageBroker.subscribe(subscription.getTopic(), subscription.getSubscriber());
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody Subscription subscription) throws SubscriberNotExistsException, TopicNotExistsException {
        messageBroker.unsubscribe(subscription.getTopic(), subscription.getSubscriber());
    }

    @PostMapping("/publish")
    public void publish(@RequestBody Message message) throws TopicNotExistsException {
        messageBroker.publish(message);
    }

}
