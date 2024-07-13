package org.wolf.role.impl;

public class Witch implements org.wolf.role.Witch {
    @Override
    public boolean hasAnyMedicine() {
        return false;
    }

    @Override
    public boolean alive() {
        return false;
    }
}
