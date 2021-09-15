package org.pubsub.web;

import org.pubsub.broker.Message;
import org.pubsub.broker.MessageBroker;
import org.pubsub.exception.AlreadySubscribedException;
import org.pubsub.exception.SubscriberNotExistsException;
import org.pubsub.exception.TopicNotExistsException;
import org.pubsub.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1")
public class MessageBrokerEndpoint {

    @Autowired
    private MessageBroker messageBroker;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Subscription subscription) throws AlreadySubscribedException {
        subscription.getSubscriber().setRestTemplate(restTemplate);
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
