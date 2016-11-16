package main.java.com.rps.net.protocol;

import ropasci.net.Peer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class SendHandler implements Runnable {
    private static final Logger log = Logger.getLogger(SendHandler.class.getName());

    private final Peer peer;
    private final DataOutputStream out;
    private boolean alive;
    private ConcurrentLinkedQueue<RPSMessage> sendQueue = new ConcurrentLinkedQueue<>();

    public SendHandler(Peer peer, DataOutputStream dataOut) {
        this.peer = peer;
        this.out = dataOut;
        alive = true;
    }

    @Override
    public void run() {
        try {
            while (alive && out != null) {
                    while (sendQueue.isEmpty()) {
                        out.flush();
                    }
                RPSMessage msg;
                while ((msg = sendQueue.poll()) != null) {
                    msg.send(out);
                }
            }
        } catch (IOException e) {
            alive = false;
            e.printStackTrace();
        }
    }

    public void send(RPSMessage message) {
        addToSendQueue(message);
    }

    private void addToSendQueue(RPSMessage data) {
        sendQueue.add(data);
    }
}
