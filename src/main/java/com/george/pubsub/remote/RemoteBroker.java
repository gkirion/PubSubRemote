package com.george.pubsub.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.http.HttpBuilder;
import com.george.http.HttpRequest;
import com.george.http.HttpResponse;
import pubsub.broker.Brokerable;
import pubsub.broker.Message;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteBroker {

    private String ip;
    private int port;
    private Brokerable broker;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private ObjectMapper mapper;
    private Set<RemoteAddress> remoteBrokers;
    private Map<String, Set<RemoteAddress>> remoteSubscribers;
    private Map<String, Set<RemoteAddress>> remotePublishers;

    public RemoteBroker(int port) throws IOException {
        this("localhost", port);
    }

    public RemoteBroker(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        mapper = new ObjectMapper();
        remoteBrokers = new HashSet<>();
        remoteSubscribers = new HashMap<>();
        remotePublishers = new HashMap<>();
        serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
        System.out.println("started remote broker... listening on ip=" + serverSocket.getInetAddress() + " port=" + serverSocket.getLocalPort());
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String line = bufferedReader.readLine();
                        System.out.println(line);
                        String type = line.split(" ")[1].substring(1);
                        int len = 0;
                        line = bufferedReader.readLine();
                        while(!line.isEmpty()) {
                            System.out.println(line);
                            if (line.startsWith("Content-Length")) {
                                String[] tokens = line.split(":");
                                System.out.println(tokens[0]);
                                System.out.println(tokens[1]);
                                System.out.println(len = Integer.parseInt(tokens[1].trim()));
                            }
                            line = bufferedReader.readLine();
                        }
                        if (len > 0) {
                            char[] buf = new char[len];
                            System.out.println(bufferedReader.read(buf,0, len));
                            line = String.copyValueOf(buf);
                        }
                        if (type.equals("publish")) {
                            Message message = mapper.readValue(line, Message.class);
                            System.out.println(message);
                            if (broker != null) {
                                broker.publish(message.getTopic(), message.getText());
                            }
                            clientSocket.getOutputStream().write("HTTP/1.1 200 OK\n\n".getBytes("UTF-8"));
                        } else if (type.equals("subscribe")) {
                            RemoteSubscription remoteSubscription = mapper.readValue(line, RemoteSubscription.class);
                            if (!remoteSubscribers.containsKey(remoteSubscription.getTopic())) {
                                remoteSubscribers.put(remoteSubscription.getTopic(), new HashSet<>());
                            }
                            remoteSubscribers.get(remoteSubscription.getTopic()).add(remoteSubscription.getRemoteSubscriber());
                            System.out.println(remoteSubscription);
                            clientSocket.getOutputStream().write("HTTP/1.1 200 OK\n\n".getBytes("UTF-8"));
                        } else if (type.equals("update")) {
                            remoteBrokers = mapper.readValue(line, new TypeReference<Set<RemoteAddress>>(){});
                            System.out.println("updated remote broker list");
                            System.out.println(remoteBrokers);
                            clientSocket.getOutputStream().write("HTTP/1.1 200 OK\n\n".getBytes("UTF-8"));
                        } else {
                            clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\n\n".getBytes("UTF-8"));
                        }
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public Brokerable getBroker() {
        return broker;
    }

    public void setBroker(Brokerable broker) {
        this.broker = broker;
    }

    public boolean register(String thirorosIp, int thirorosPort) throws IOException {
        System.out.println("connecting to thiroros...ip=" + thirorosIp + " port=" + thirorosPort);
        HttpRequest http = new HttpRequest(thirorosIp, thirorosPort);
        RemoteAddress remoteAddress = new RemoteAddress(ip, port);
        String str = mapper.writeValueAsString(remoteAddress);
        http.setContent(str);
        HttpResponse response = http.get("register");
        if (response.getResponseCode() == 200) {
            System.out.println(response.getResponseBody());
            remoteBrokers = mapper.readValue(response.getResponseBody(), new TypeReference<Set<RemoteAddress>>(){});
            System.out.println("registered successfully with thiroros");
            System.out.println("remote brokers: " + remoteBrokers);
            return true;
        } else {
            System.out.println("failed to register with thiroros");
            return false;
        }
    }

    public boolean unregister(String thirorosIp, int thirorosPort) throws IOException {
        System.out.println("connecting to thiroros...ip=" + thirorosIp + " port=" + thirorosPort);
        HttpRequest http = new HttpRequest(thirorosIp, thirorosPort);
        HttpResponse response = http.get("unregister");
        if (response.getResponseCode() == 200) {
            System.out.println("unregistered successfully with thiroros");
            return true;
        } else {
            System.out.println("failed to unregister from thiroros");
            return false;
        }
    }

    public void publish(String topic, String text) {
        if (remoteSubscribers.containsKey(topic)) {
            for (RemoteAddress remoteSubscriber : remoteSubscribers.get(topic)) {
                try {
                    Message message = new Message(topic, text);
                    HttpResponse response = HttpBuilder.get(remoteSubscriber.getIp(), remoteSubscriber.getPort(), "publish")
                            .body(mapper.writeValueAsString(message)).send();
                    System.out.println(response);
                } catch (IOException e) {
                    System.out.println("could not connect to the broker server...");
                    System.out.println("reason: " + e.getMessage());
                }
            }
        }
    }

    public boolean subscribe(String topic) {
        for (RemoteAddress remoteBroker : remoteBrokers) {
            if (!remotePublishers.containsKey(topic) || !remotePublishers.get(topic).contains(remoteBroker)) {
                try {
                    RemoteSubscription remoteSubscription = new RemoteSubscription();
                    remoteSubscription.setTopic(topic);
                    remoteSubscription.setRemoteSubscriber(new RemoteAddress(ip, port));
                    HttpResponse response = HttpBuilder.get(remoteBroker.getIp(), remoteBroker.getPort(), "subscribe")
                            .body(mapper.writeValueAsString(remoteSubscription)).send();

                    if (response.getResponseCode() == 200) {
                        if (!remotePublishers.containsKey(topic)) {
                            remotePublishers.put(topic, new HashSet<>());
                        }
                        remotePublishers.get(topic).add(remoteBroker);
                        return true;
                    }
                } catch (IOException e) {
                    System.out.println("could not connect to remote broker...");
                    System.out.println("reason: " + e.getMessage());
                }
            }
        }
        return false;
    }

    public void shutdown() throws IOException {
        serverSocket.close();
        executor.shutdown();
    }
}
