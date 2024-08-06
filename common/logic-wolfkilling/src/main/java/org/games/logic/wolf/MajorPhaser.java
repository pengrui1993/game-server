package org.games.logic.wolf;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.core.Minor;
import org.games.logic.wolf.core.Phaser;

public abstract class MajorPhaser implements Phaser<Major> {

    public Minor minor(){ return null;}
    public String speaker(){ return null;}
    public String curLastWorldUser(){ return null;}
}
