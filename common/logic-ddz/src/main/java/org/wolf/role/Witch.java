package org.wolf.role;

public interface Witch extends Role{
    boolean hasAnyMedicine();
    @Override
    default Roles role(){ return Roles.WITCH;}
}