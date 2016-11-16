package main.java.com.rps.net.protocol;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class RPSMessage {
    private static final Logger log = Logger.getLogger(RPSMessage.class.getName());

    public final static byte HEARTBEAT = 0;
    public final static byte HANDSHAKE = 1;
    public final static byte ACTION = 2;
    public final static byte DISCONNECT = 3;
    public final static byte QUERY_PEERS = 4;

    public final static String[] operations = {
            "Heartbeat",
            "Handshake",
            "Action",
            "Disconnect"
    };

    public final static byte ACTION_ROCK = 1;
    public final static byte ACTION_PAPER = 2;
    public final static byte ACTION_SCISSORS = 3;

    public final static String[] actions = {
            "Rock",
            "Paper",
            "Scissors"
    };

    private byte type;
    private byte[] data;
    private int length;

    public RPSMessage(byte type) {
        this.type = type;
        this.length = 1;
    }

    public RPSMessage(byte type, byte[] data) {
        this.type = type;
        this.data = data;
        this.length = 1 + data.length;
    }

    public void send(DataOutputStream out) throws IOException {
        log.info("Sending data (len=" + length + ", type=" + type +")");

        out.writeInt(length);
        out.writeByte(type);
        if (data != null && data.length > 0) {
            out.write(data);
        }
    }

    public byte getType() {
        return type;
    }
}
