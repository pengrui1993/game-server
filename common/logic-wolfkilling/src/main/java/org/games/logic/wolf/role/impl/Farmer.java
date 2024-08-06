package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Farmer implements org.games.logic.wolf.role.Farmer {
    @Override
    public boolean alive() {
        return lived;
    }
    private final Context ctx;
    public Farmer(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    private boolean lived;

    @Override
    public String toString() {
        return info();
    }
}
