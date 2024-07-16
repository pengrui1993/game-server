package org.wolf.role;

public interface Hunter extends Role{
    @Override
    default Roles role(){ return Roles.HUNTER;}

    boolean killed();
}
