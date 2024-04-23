package io.chat.live.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.chat.live.domain.RoomEventData;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JsonSerializer {

    private final Gson gson;

    public JsonSerializer() {
        this.gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .registerTypeAdapter(RoomEventData.class, new EventDataAdapter())
            .create();
    }

    public String toJson(final Object source) {
        return gson.toJson(source);
    }

    public <T> T fromJson(final String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

}
