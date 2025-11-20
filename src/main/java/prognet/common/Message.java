package prognet.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Message {
    private MessageType type;
    private JsonObject data;

    private static final Gson gson = new Gson();

    public Message(MessageType type, JsonObject data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public JsonObject getData() {
        return data;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public static Message fromJson(String json) {
        return gson.fromJson(json, Message.class);
    }

    public String getDataString(String key) {
        return data.has(key) ? data.get(key).getAsString() : null;
    }

    public int getDataInt(String key) {
        return data.has(key) ? data.get(key).getAsInt() : 0;
    }

    public boolean getDataBoolean(String key) {
        return data.has(key) ? data.get(key).getAsBoolean() : false;
    }
}
