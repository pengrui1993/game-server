package org.wolf.role.impl;

import org.wolf.core.Context;

import java.util.HashMap;
import java.util.Map;

public class Predictor implements org.wolf.role.Predictor {
    @Override
    public boolean alive() {
        return false;
    }
    private final Context ctx;
    public Predictor(Context ctx) {
        this.ctx = ctx;
    }
    public final Map<String,Boolean> verifies = new HashMap<>();
}
