package org.games.event;

import com.google.gson.*;

import java.lang.reflect.Type;

public class EventDeserializer implements JsonDeserializer<AbstractEvent>{
    @Override
    public AbstractEvent deserialize(JsonElement je, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = je.getAsJsonObject();
        String clazz = obj.get("@class").getAsString();
        try {
            return ctx.deserialize(obj,Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

}
