package org.wolf.race;


import org.wolf.WolfKilling;

import java.util.List;

public interface Context extends org.wolf.core.Context<Minor,MinorPhaser,Context> {
    WolfKilling top();
    List<String> joinedUsers();

    void setSergeant(String sergeant);
}
