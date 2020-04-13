package launcher;

import com.george.pubsub.remote.RemoteBroker;
import pubsub.broker.Broker;
import pubsub.broker.Message;
import pubsub.broker.Receivable;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        RemoteBroker remoteBroker = new RemoteBroker(15000);
        RemoteBroker remoteBroker2 = new RemoteBroker(15001);
        remoteBroker.register("localhost", 50000);
        remoteBroker2.register("localhost", 50000);
        remoteBroker.register("localhost", 50000);
        Broker localBroker = new Broker();
        Broker localBroker2 = new Broker();
        localBroker.subscribe("test", new Receivable() {
            @Override
            public void receive(Message message) {
                System.out.println("local subscriber: " + message);
            }
        });
        remoteBroker.setBroker(localBroker);
        localBroker.publish("test", "hello world!");
        remoteBroker.subscribe("test");
        remoteBroker.subscribe("test");
        remoteBroker.subscribe("test");
        remoteBroker.subscribe("test");

        remoteBroker.publish("test", "hello world!");
        remoteBroker2.publish("test", "hello \nyou too!");
        /*
        Socket client = new Socket();
        client.connect(new InetSocketAddress("localhost", 15000));
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(client.getOutputStream());
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write("GET / HTTP/1.1\n");
        bufferedWriter.flush();
        InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            System.out.println(line);
            line = bufferedReader.readLine();
        }
        client.close();
        */
    }

}
