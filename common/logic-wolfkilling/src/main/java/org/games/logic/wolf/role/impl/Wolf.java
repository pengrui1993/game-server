package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Wolf implements org.games.logic.wolf.role.Wolf {
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
    public Wolf(Context ctx) {
        this.ctx = ctx;
        this.lived = true;
    }
    @Override
    public String toString() {
        return info();
    }
}
