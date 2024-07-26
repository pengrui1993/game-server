package org.wolf.role.impl;

import org.wolf.core.Context;

public class Wolf implements org.wolf.role.Wolf {
    private final Context ctx;
    public Wolf(Context ctx) {
        this.ctx = ctx;
        this.lived = true;
    }
    public boolean lived;
    @Override
    public boolean alive() {
        return lived;
    }
    @Override
    public String toString() {
        return info();
    }
}
