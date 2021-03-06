package com.george.pubsub.remote;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.http.HttpBuilder;
import com.george.http.HttpRequest;
import com.george.http.HttpResponse;
import pubsub.broker.Broker;
import pubsub.broker.Brokerable;
import pubsub.broker.Message;
import pubsub.broker.Receivable;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteBroker {

    private String ip;
    private int port;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private ObjectMapper mapper;
    private Broker broker;

    public RemoteBroker(int port) throws IOException {
        this("localhost", port);
    }

    public RemoteBroker(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        mapper = new ObjectMapper();
        broker = new Broker();
        serverSocket = new ServerSocket(port, 50, InetAddress.getByName(ip));
        System.out.println("started remote broker... listening on ip=" + serverSocket.getInetAddress() + " port=" + serverSocket.getLocalPort());
        executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try (Socket clientSocket = serverSocket.accept()) {
                        InputStreamReader inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String line = bufferedReader.readLine();
                        //System.out.println(line);
                        String type = line.split(" ")[1].substring(1);
                        int len = 0;
                        line = bufferedReader.readLine();
                        while (!line.isEmpty()) {
                            //System.out.println(line);
                            if (line.startsWith("Content-Length")) {
                                String[] tokens = line.split(":");
                          //      System.out.println(tokens[0]);
                            //    System.out.println(tokens[1]);
                              //  System.out.println(len = Integer.parseInt(tokens[1].trim()));
                                len = Integer.parseInt(tokens[1].trim());
                            }
                            line = bufferedReader.readLine();
                        }
                        if (len > 0) {
                            char[] buf = new char[len];
                            bufferedReader.read(buf,0, len);
                           // System.out.println(bufferedReader.read(buf,0, len));
                            line = String.copyValueOf(buf);
                        }
                        if (type.equals("publish")) {
                            Message message = mapper.readValue(line, Message.class);
                            //System.out.println(message);
                            broker.publish(message);
                            clientSocket.getOutputStream().write("HTTP/1.1 200 OK\n\n".getBytes("UTF-8"));
                        } else if (type.equals("subscribe")) {
                            RemoteSubscription remoteSubscription = mapper.readValue(line, RemoteSubscription.class);
                            RemoteSubscriber remoteSubscriber = new RemoteSubscriber(remoteSubscription.getRemoteSubscriber());
                            remoteSubscriber.setMapper(mapper);
                            broker.subscribe(remoteSubscription.getTopic(), remoteSubscriber);
                            System.out.println(remoteSubscription);
                            clientSocket.getOutputStream().write("HTTP/1.1 200 OK\n\n".getBytes("UTF-8"));
                        } else {
                            clientSocket.getOutputStream().write("HTTP/1.1 404 Not Found\n\n".getBytes("UTF-8"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void shutdown() throws IOException {
        serverSocket.close();
        executor.shutdown();
        broker.shutdown();
    }
}
