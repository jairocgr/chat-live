package io.chat.live.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.chat.live.domain.Message;
import io.chat.live.domain.RoomEventData;

import java.lang.reflect.Type;
import java.time.Instant;

public class EventDataAdapter implements JsonDeserializer<RoomEventData> {

    @Override
    public RoomEventData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {

        if (element.isJsonNull()) {
            return null;
        }

        var object = element.getAsJsonObject();
        if (object.has("content")) {
            return Message.builder()
                .content(object.get("content").getAsString())
                .time(Instant.parse(object.get("time").getAsString()))
                .build();
        } else {
            throw new JsonParseException("Invalid " + RoomEventData.class.getSimpleName());
        }
    }
}
