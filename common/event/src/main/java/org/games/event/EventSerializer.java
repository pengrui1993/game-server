package org.games.event;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EventSerializer implements JsonSerializer<AbstractEvent> {
    @Override
    public JsonElement serialize(AbstractEvent event, Type type, JsonSerializationContext ctx) {
        JsonElement serialize = ctx.serialize(event,AbstractEvent.class);
        JsonObject obj = serialize.getAsJsonObject();
        obj.add("eventTypeId",new JsonPrimitive(event.type().id));
        obj.add("@class",new JsonPrimitive(event.getClass().getCanonicalName()));
        return obj;
    }
}
