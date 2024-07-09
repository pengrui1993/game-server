package org.games;

import org.games.event.*;

public class GsonTest {
    public static void main(String[] args) {
        String json = new NodeConnectEvent().toJson();
        System.out.println(json);
        Event evt = EventUtils.g.fromJson(json, AbstractEvent.class);
        System.out.println(evt.type());
    }
}
