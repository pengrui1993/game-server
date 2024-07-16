package org.wolf.role.impl;

import org.wolf.core.Context;

import java.util.Objects;

public class Hunter implements org.wolf.role.Hunter {
    @Override
    public boolean alive() {
        return lived;
    }
    private final Context ctx;
    public Hunter(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    private boolean lived;
    public String killedUserId;
    @Override
    public boolean killed() {
        return Objects.nonNull(killedUserId);
    }
}
