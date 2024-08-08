package org.games.logic.wolf.role.impl;

import org.games.logic.wolf.core.Context;

import java.util.HashMap;
import java.util.Map;

public class Predictor implements org.games.logic.wolf.role.Predictor {
    @Override
    public boolean alive() {
        return lived;
    }

    @Override
    public boolean goDied() {
        boolean alreadyDied = !lived;
        lived = false;
        return alreadyDied;
    }
    private boolean lived;

    private final Context ctx;
    public Predictor(Context ctx) {
        this.ctx = ctx;
        lived = true;
    }
    public final Map<String,Boolean> verifies = new HashMap<>();
    @Override
    public String toString() {
        return info();
    }
}
