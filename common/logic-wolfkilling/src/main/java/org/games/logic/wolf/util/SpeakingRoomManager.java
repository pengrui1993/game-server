package org.games.logic.wolf.util;

import java.util.List;

public interface SpeakingRoomManager {
    SpeakingRoom create(List<String> joinedUsers);
    void destroy(SpeakingRoom room);
    void update(float dt);
}
