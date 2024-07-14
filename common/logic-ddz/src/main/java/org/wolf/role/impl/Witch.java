package org.wolf.role.impl;

import org.wolf.core.Context;

public class Witch implements org.wolf.role.Witch {
    private final Context ctx;
    public Witch(Context ctx) {
        this.ctx = ctx;
    }
    @Override
    public boolean alive() {
        return false;
    }

    @Override
    public boolean hasDrug() {
        return false;
    }

    @Override
    public boolean hasMedicine() {
        return false;
    }
}
