package org.games.logic.wolf.race;

import org.games.logic.wolf.core.Minor;
import org.games.logic.wolf.core.Phaser;

public abstract class MinorPhaser implements Phaser<Minor> {

    public static MinorPhaser init(Context ctx) {
        return new HandsUpPhaser(ctx);
    }
    public String speaker(){return null;}
}
