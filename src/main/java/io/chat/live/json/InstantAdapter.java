package io.chat.live.json;

import com.google.gson.JsonSerializer;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;

public class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    @Override
    public Instant deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        var string = element.getAsString();
        return Instant.parse(string);
    }

    @Override
    public JsonElement serialize(Instant instant, Type type, JsonSerializationContext context) {
        var formatted = instant.toString();
        return new JsonPrimitive(formatted);
    }
}
