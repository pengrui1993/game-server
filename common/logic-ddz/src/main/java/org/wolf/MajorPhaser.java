package org.wolf;

import org.wolf.core.Phaser;
import org.wolf.race.Minor;

public abstract class MajorPhaser implements Phaser<Major> {

    public Minor minor(){ return null;}
}
