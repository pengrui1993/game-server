package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Farmer implements org.games.logic.wolf.role.Farmer {
    @Override
    public boolean alive() {
        return lived;
    }

    @Override
    public boolean goDied() {
        boolean alreadyDied = !lived;
        lived = false;
        return alreadyDied;
    }
    private boolean lived;

    private final Context ctx;
    public Farmer(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }

    @Override
    public String toString() {
        return info();
    }
}
