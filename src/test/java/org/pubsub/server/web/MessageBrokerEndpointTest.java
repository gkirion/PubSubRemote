package org.pubsub.server.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.pubsub.core.broker.exception.AlreadySubscribedException;
import org.pubsub.core.broker.exception.SubscriberNotExistsException;
import org.pubsub.core.broker.exception.TopicNotExistsException;
import org.pubsub.core.broker.service.MessageBroker;
import org.pubsub.core.message.Message;
import org.pubsub.core.message.MessageBuilder;
import org.pubsub.core.subscriber.Subscriber;
import org.pubsub.server.subscriber.RemoteSubscriberBuilder;
import org.pubsub.server.subscription.Subscription;
import org.pubsub.server.subscription.SubscriptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(MessageBrokerEndpoint.class)
public class MessageBrokerEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageBroker messageBroker;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(mockMvc);
    }

    @Test
    public void alreadySubscibedTest() throws Exception {
        Mockito.doThrow(new AlreadySubscribedException()).when(messageBroker).subscribe(Mockito.any(String.class), Mockito.any(Subscriber.class));
        Subscription subscription = new SubscriptionBuilder()
                .topic("")
                .subscriber(
                        new RemoteSubscriberBuilder()
                                .address("")
                                .port(0)
                                .build())
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/subscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                        .writeValueAsString(subscription)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof AlreadySubscribedException));
    }

    @Test
    public void unsubscribeTopicNotExistsTest() throws Exception {
        Mockito.doThrow(new TopicNotExistsException()).when(messageBroker).unsubscribe(Mockito.any(String.class), Mockito.any(Subscriber.class));
        Subscription subscription = new SubscriptionBuilder()
                .topic("")
                .subscriber(
                        new RemoteSubscriberBuilder()
                                .address("")
                                .port(0)
                                .build())
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/unsubscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                        .writeValueAsString(subscription)))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof TopicNotExistsException));
    }

    @Test
    public void unsubscribeSubscriberNotExistsTest() throws Exception {
        Mockito.doThrow(new SubscriberNotExistsException()).when(messageBroker).unsubscribe(Mockito.any(String.class), Mockito.any(Subscriber.class));
        Subscription subscription = new SubscriptionBuilder()
                .topic("")
                .subscriber(
                        new RemoteSubscriberBuilder()
                                .address("")
                                .port(0)
                                .build())
                .build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/unsubscribe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                        .writeValueAsString(subscription)))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof SubscriberNotExistsException));
    }

    @Test
    public void publishTopicNotExistsTest() throws Exception {
        Mockito.doThrow(new TopicNotExistsException()).when(messageBroker).publish(Mockito.any(Message.class));
        Message message = new MessageBuilder().topic("").text("").build();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/publish")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper
                        .writeValueAsString(message)))
                .andDo(MockMvcResultHandlers.print()).andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(mvcResult -> Assertions.assertTrue(mvcResult.getResolvedException() instanceof TopicNotExistsException));
    }

}
