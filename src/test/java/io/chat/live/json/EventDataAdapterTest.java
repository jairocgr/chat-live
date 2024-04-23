package io.chat.live.json;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.chat.live.domain.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventDataAdapterTest {

    EventDataAdapter adapter;

    Gson gson = new Gson();

    @BeforeEach
    void setUp() {
        adapter = new EventDataAdapter();
    }

    @Test
    void testNullConversion() {
        var element = JsonNull.INSTANCE;
        var data = adapter.deserialize(element, null, null);
        assertNull(data);
    }

    @Test
    void testMessageDeserialization() {
        var epoch = 1713223555; // GMT Monday, 15 April 2024 23:25:55
        var time = Instant.ofEpochSecond(epoch);
        var expected = Message.builder()
            .time(time)
            .content("Repudiandae sint et")
            .build();

        var object = new JsonObject();
        object.addProperty("time", "2024-04-15T23:25:55Z");
        object.addProperty("content", "Repudiandae sint et");

        var data = adapter.deserialize(object, null, null);

        assertThat(data)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    @Test
    void testUnknownJson() {
        // Invalid/unknown JSON
        var object = new JsonObject();
        object.addProperty("message", "Repudiandae sint et");
        assertThrows(JsonParseException.class, () -> {
            adapter.deserialize(object, null, null);
        });
    }
}
