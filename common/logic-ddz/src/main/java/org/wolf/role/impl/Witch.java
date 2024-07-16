package org.wolf.role.impl;

import org.wolf.core.Context;

public class Witch implements org.wolf.role.Witch {
    private final Context ctx;
    public boolean drug;
    public boolean medicine;
    public Witch(Context ctx) {
        this.ctx = ctx;
        this.lived =drug=medicine= true;
    }
    private boolean lived;
    @Override
    public boolean alive() {
        return lived;
    }

    @Override
    public boolean hasDrug() {
        return drug;
    }

    @Override
    public boolean hasMedicine() {
        return medicine;
    }
}
