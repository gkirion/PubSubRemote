package launcher;

import com.george.pubsub.remote.RemoteBroker;
import pubsub.broker.Broker;
import pubsub.broker.Message;
import pubsub.broker.Receivable;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        RemoteBroker remoteBroker = new RemoteBroker(15000);
    }

}
