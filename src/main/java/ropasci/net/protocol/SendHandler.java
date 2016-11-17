package ropasci.net.protocol;

import ropasci.net.Peer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class SendHandler implements Runnable {
    private static final Logger log = Logger.getLogger(SendHandler.class.getName());
    private static final long THROTTLE_MS = 1000; // Used to throttle the isEmpty() while-loop


    private final Peer peer;
    private final DataOutputStream out;
    private boolean alive;
    private ConcurrentLinkedQueue<RPSMessage> sendQueue = new ConcurrentLinkedQueue<>();
    private Thread th;

    public SendHandler(Peer peer, DataOutputStream dataOut) {
        this.peer = peer;
        this.out = dataOut;
        alive = true;
    }

    @Override
    public void run() {
        th = Thread.currentThread();
        try {
            while (alive && out != null) {
                    while (sendQueue.isEmpty()) {
                        out.flush();
                        // TODO Maybe use notify/wait?
                        Thread.sleep(THROTTLE_MS);
                    }
                RPSMessage msg;
                while ((msg = sendQueue.poll()) != null) {
                    msg.send(out);
                }
            }
        } catch (IOException e) {
            alive = false;
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            peer.disconnect();
        }
    }

    public void disconnect() {
        alive = false;
        if (th != null) {
            th.interrupt();
        }
        sendQueue.clear();
    }
    public void send(RPSMessage message) {
        addToSendQueue(message);
    }

    private void addToSendQueue(RPSMessage data) {
        sendQueue.add(data);
    }
}
