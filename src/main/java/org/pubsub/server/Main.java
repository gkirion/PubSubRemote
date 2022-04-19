package org.pubsub.server;

import org.pubsub.core.broker.service.MessageBroker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public MessageBroker getMessageBroker() {
        return new MessageBroker();
    }

}
