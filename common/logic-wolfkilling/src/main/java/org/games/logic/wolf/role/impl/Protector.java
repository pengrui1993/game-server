package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Protector implements org.games.logic.wolf.role.Protector {
    private final Context ctx;
    public Protector(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    public String lastProtectedUserId;
    private boolean lived;
    @Override
    public boolean alive() {
        return lived;
    }
    @Override
    public String toString() {
        return info();
    }
}
