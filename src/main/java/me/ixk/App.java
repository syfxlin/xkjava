package me.ixk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.utils.JWT;

public class App {

    public static void main(String[] args) {
        JWT jwt = new JWT("123", "HS256");
        Map<String, String> payload = new ConcurrentHashMap<>();
        payload.put("key", "value");
        String token = jwt.encode(payload);
        System.out.println(jwt.decode(token));
    }
}
