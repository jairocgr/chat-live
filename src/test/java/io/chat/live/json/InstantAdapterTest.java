package io.chat.live.json;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InstantAdapterTest {

    InstantAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new InstantAdapter();
    }

    @Test
    void shouldConvertInstantToJsonString() {
        var epoch = 1713223555; // GMT Monday, 15 April 2024 23:25:55
        var now = Instant.ofEpochSecond(epoch);
        var element = adapter.serialize(now, null, null);
        assertTrue(element.isJsonPrimitive());
        assertEquals("2024-04-15T23:25:55Z", element.getAsString());
    }

    @Test
    void shouldConvertStringToInstant() {
        var string = "2024-04-15T23:25:55Z";
        var epoch = 1713223555 * 1000L;
        var element = new JsonPrimitive(string);
        var instant = adapter.deserialize(element, null, null);
        assertEquals(epoch, instant.toEpochMilli());
        assertEquals("2024-04-15T23:25:55Z", instant.toString());
    }

}
