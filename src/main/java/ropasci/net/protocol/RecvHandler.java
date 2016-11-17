package ropasci.net.protocol;

import ropasci.net.Peer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class RecvHandler implements Runnable {
    private static final Logger log = Logger.getLogger(RecvHandler.class.getName());

    private final Peer peer;
    private final DataInputStream in;
    private boolean alive;
    private Thread th;

    public RecvHandler(Peer peer, DataInputStream dataIn) {
        this.peer = peer;
        this.in = dataIn;
        alive = true;
    }

    @Override
    public void run() {
        log.info("Receive handler, handling my stuff!");
        th = Thread.currentThread();
        try {
            while (alive && in != null) {
                int dataLength = in.readInt();
                if (dataLength < 0) {
                    log.severe("Improper data on input, skipping...");
                    throw new IOException("Unexpected data length.");
                }
                byte type = in.readByte();
                switch (type) {
                    case RPSMessage.HEARTBEAT:
                        log.info("Received HEARTBEAT");
                        break;
                    case  RPSMessage.DISCONNECT:
                        log.info("Received DISCONNECT");
                        peer.getConnection().onReceive(peer, new RPSMessage(RPSMessage.DISCONNECT));
                        break;
                    case RPSMessage.ACTION:
                        log.info("Received action!");
                        byte[] bytes = new byte[dataLength - 1];
                        in.readFully(bytes);
                        System.out.println(bytes);
                        // TODO Create well-formed message, pass it up
                        int actionIndex = RPSMessage.ACTION - 1;
                        peer.getConnection().onReceiveCommand(
                                peer,
                                RPSMessage.operations[actionIndex],
                                RPSMessage.actions[bytes[0]]
                        );

                }
            }
        } catch (IOException ioEx) {
            // Ignore
        } catch (Exception ex) {
            ex.printStackTrace();
            alive = false;
        } finally {
            peer.disconnect();
        }
    }

    public void disconnect() {
        alive = false;
        if (th != null) {
            th.interrupt();
        }
    }
}
