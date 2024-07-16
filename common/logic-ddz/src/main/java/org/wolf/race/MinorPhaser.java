package org.wolf.race;

import org.wolf.core.Phaser;

public abstract class MinorPhaser implements Phaser<Minor> {

    public static MinorPhaser init(Context ctx) {
        return new HandsUpPhaser(ctx);
    }
}
