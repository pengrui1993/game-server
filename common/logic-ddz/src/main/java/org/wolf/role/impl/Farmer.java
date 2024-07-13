package org.wolf.role.impl;

import org.wolf.core.Context;

public class Farmer implements org.wolf.role.Farmer {
    @Override
    public boolean alive() {
        return false;
    }
    private final Context ctx;
    public Farmer(Context ctx) {
        this.ctx = ctx;
    }
}
