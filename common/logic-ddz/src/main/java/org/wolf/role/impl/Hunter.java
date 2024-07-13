package org.wolf.role.impl;

import org.wolf.core.Context;

public class Hunter implements org.wolf.role.Hunter {
    @Override
    public boolean alive() {
        return false;
    }
    private final Context ctx;
    public Hunter(Context ctx) {
        this.ctx = ctx;
    }
}
