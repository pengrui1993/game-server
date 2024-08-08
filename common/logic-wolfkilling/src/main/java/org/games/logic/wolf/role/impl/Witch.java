package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

public class Witch implements org.games.logic.wolf.role.Witch {
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
    public boolean drug;
    public boolean medicine;
    public Witch(Context ctx) {
        this.ctx = ctx;
        this.lived =drug=medicine= true;
    }
    @Override
    public boolean hasDrug() {
        return drug;
    }

    @Override
    public boolean hasMedicine() {
        return medicine;
    }
    @Override
    public String toString() {
        return info();
    }
}
