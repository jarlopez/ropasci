package main.java.com.rps.sandbox;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketListener {
    public static final int DEFAULT_PORT = 1028;

    private final int port;
    private final ExecutorService exec;
    private final Logger logger ;
    private final ListProperty<PeerConnection> peerData = new SimpleListProperty<>(this, "peerData", FXCollections.observableArrayList());
    private final StringProperty status= new SimpleStringProperty(ConnectionStatus.OFFLINE.name());

    public final ListProperty<PeerConnection> peerDataProperty() {
        return this.peerData;
    }
    public final List<PeerConnection> getConnections() {
        return this.peerDataProperty().get();
    }
    public final StringProperty statusProperty() {
        return this.status;
    }
    public final String getStatus() {
        return this.statusProperty().get();
    }

    public SocketListener(int port) throws IOException {
        this.port = port;

        this.exec = Executors.newCachedThreadPool(runnable -> {
            // run thread as daemon:
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        this.logger = Logger.getLogger("SocketListener");

        try {
            startListening();
        } catch (IOException exc) {
            logger.log(Level.SEVERE, "Exception in listener", exc);
            throw exc;
        }
    }
    public void startListening() throws IOException {
        Callable<Void> connectionListener = () -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.info(
                        "Server listening on " + serverSocket.getInetAddress()
                                + ":" + serverSocket.getLocalPort());
                status.setValue(ConnectionStatus.ONLINE.name());
                while (true) {
                    logger.info( "Waiting for connection:");
                    Socket socket = serverSocket.accept();
                    logger.info( "Connection accepted from " + socket.getInetAddress());
                    handleConnection(socket);
                }
            } catch (Exception exc) {
                logger.log(Level.SEVERE, "Exception in connection handler", exc);
                status.setValue(ConnectionStatus.ERROR.name());
                return null;
            }
        };
        exec.submit(connectionListener);
    }

    public void shutdown() {
        exec.shutdownNow();
    }

    private void handleConnection(Socket socket) {
        Callable<Void> connectionHandler = () -> {
            logger.info("Adding now " +  socket.getInetAddress().toString() + ":" + Integer.toString(socket.getPort()));
            PeerConnection it = new PeerConnection(socket.getInetAddress().toString(), socket.getPort());
            peerData.add(it);
            logger.info(peerData.toString());
            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    logger.info("Received: " + line);
                }
                System.out.println("Connection closed from "+socket.getInetAddress());
            }
            return null;
        };
        exec.submit(connectionHandler);
    }

}
