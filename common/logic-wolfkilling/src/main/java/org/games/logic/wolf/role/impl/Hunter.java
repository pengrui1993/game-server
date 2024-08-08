package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

import java.util.Objects;

public class Hunter implements org.games.logic.wolf.role.Hunter {
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
    public Hunter(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    public String killedUserId;
    @Override
    public boolean killed() {
        return Objects.nonNull(killedUserId);
    }
    @Override
    public String toString() {
        return info();
    }
}
