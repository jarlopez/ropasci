package main.java.com.rps.utils;

public class ValidationUtil {
    public static boolean validateNotEmpty(String ip) {
        return ip != null && ip.length() != 0;
    }
}
