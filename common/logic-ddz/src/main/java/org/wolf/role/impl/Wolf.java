package org.wolf.role.impl;

import org.wolf.core.Context;

public class Wolf implements org.wolf.role.Wolf {
    @Override
    public boolean alive() {
        return false;
    }
    private final Context ctx;
    public Wolf(Context ctx) {
        this.ctx = ctx;
    }
}
