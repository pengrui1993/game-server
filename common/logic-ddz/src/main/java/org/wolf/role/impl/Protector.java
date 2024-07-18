package org.wolf.role.impl;

import org.wolf.core.Context;

public class Protector implements org.wolf.role.Protector {
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
