package org.games.logic.wolf.race;


import org.games.logic.wolf.WolfKilling;
import org.games.logic.wolf.core.Minor;

import java.util.List;

public interface Context extends org.games.logic.wolf.core.Context<Minor,MinorPhaser,Context> {
    WolfKilling top();
    List<String> joinedUsers();

    void setSergeant(String sergeant);
}
