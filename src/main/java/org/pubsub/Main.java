package org.pubsub;

import org.pubsub.broker.MessageBroker;
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