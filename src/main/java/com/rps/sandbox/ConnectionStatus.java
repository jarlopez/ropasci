package main.java.com.rps.sandbox;

import javafx.scene.paint.Color;

public enum ConnectionStatus {
    ONLINE(Color.GREENYELLOW),
    OFFLINE(Color.GRAY),
    ERROR(Color.RED),
    AWAITING_CONNECTION(Color.YELLOW),
    EXITED(Color.DARKGRAY);

    private Color colorValue;
    ConnectionStatus(Color color) {
        this.colorValue = color;
    }
    public Color getColor() {
        return colorValue;
    }
    public String getColorStr() {
        return colorValue.toString();
    }
}
