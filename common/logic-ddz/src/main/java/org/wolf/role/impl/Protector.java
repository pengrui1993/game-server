package org.wolf.role.impl;

import org.wolf.core.Context;

public class Protector implements org.wolf.role.Protector {
    @Override
    public boolean alive() {
        return false;
    }
    private final Context ctx;
    public Protector(Context ctx) {
        this.ctx = ctx;
    }
}
