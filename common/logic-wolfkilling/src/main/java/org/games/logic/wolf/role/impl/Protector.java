package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Protector implements org.games.logic.wolf.role.Protector {
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
    public Protector(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    public String lastProtectedUserId;
    @Override
    public String toString() {
        return info();
    }
}
