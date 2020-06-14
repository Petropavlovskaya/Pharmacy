package by.petropavlovskaja.pharmacy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Temp {
    String requestMethod = "GET";
    private static Map<String, String> sessionAttributes = new HashMap<>();
    private static Map<String, Object> requestParameters = new HashMap<>();

    private static class SessionContextHolder {
        public static final Temp SESSION_CONTEXT = new Temp();
    }

    public static Temp getSessionContextInstance(Map<String, String> map) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            sessionAttributes.put(key, map.get(key));
        }
        return SessionContextHolder.SESSION_CONTEXT;
    }

    @Override
    public String toString() {
        return "Temp{" +
                sessionAttributes.toString() +
                '}';
    }
}
