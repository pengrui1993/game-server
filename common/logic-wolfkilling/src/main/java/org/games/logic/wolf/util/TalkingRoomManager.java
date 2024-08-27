package org.games.logic.wolf.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TalkingRoomManager implements SpeakingRoomManager{
    private final Map<String,TalkingRoom> rooms = new ConcurrentHashMap<>();
    public TalkingRoom create(List<String> userIdList){
        TalkingRoom talkingRoom = TalkingRoom.create(userIdList);
        rooms.put(talkingRoom.joinKey,talkingRoom);
        return talkingRoom;
    }
    @Override
    public void destroy(SpeakingRoom room) {
        TalkingRoom r = (TalkingRoom)room;
        destroy(r.joinKey);
    }

    private void destroy(String id){
        TalkingRoom talkingRoom = rooms.remove(id);
        if(Objects.isNull(talkingRoom))return;
        talkingRoom.close();
    }
    float last;
    @Override
    public void update(float dt){
        last+=dt;
        rooms.values().forEach(e-> e.update(dt));
        rooms.values()
                .stream()
                .filter(TalkingRoom::isTimeout)
                .map(f->f.joinKey)
                .forEach(this::destroy);
    }
    private TalkingRoomManager(){}

    public static final TalkingRoomManager MGR = new TalkingRoomManager();
}
