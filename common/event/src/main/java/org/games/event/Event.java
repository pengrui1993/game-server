package org.games.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.games.constant.EventType;

public interface Event {
    EventType type();
    default long now(){return System.currentTimeMillis();}

    default String toJson(){
        return EventUtils.g.toJson(this);
    }
}
