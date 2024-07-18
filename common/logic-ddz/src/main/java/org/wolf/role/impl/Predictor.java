package org.wolf.role.impl;

import org.wolf.core.Context;

import java.util.HashMap;
import java.util.Map;

public class Predictor implements org.wolf.role.Predictor {
    @Override
    public boolean alive() {
        return lived;
    }
    private final Context ctx;
    public Predictor(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    private boolean lived;
    public final Map<String,Boolean> verifies = new HashMap<>();
    @Override
    public String toString() {
        return info();
    }
}
