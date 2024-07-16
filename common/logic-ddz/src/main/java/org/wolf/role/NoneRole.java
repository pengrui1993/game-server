package org.wolf.role;

public class NoneRole implements Role{
    @Override
    public Roles role() {
        return Roles.NONE;
    }
    @Override
    public boolean alive() {
        return false;
    }
    public static final NoneRole NONE = new NoneRole();
    private NoneRole(){}
}
