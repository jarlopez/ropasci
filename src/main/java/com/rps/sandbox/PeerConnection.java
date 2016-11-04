package main.java.com.rps.sandbox;

public class PeerConnection {
    private String host;
    private Integer port;
    private ConnectionStatus status = ConnectionStatus.OFFLINE;

    public PeerConnection(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}
