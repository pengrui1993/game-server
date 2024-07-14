package org.wolf.role;

public interface Witch extends Role{
    default boolean hasAnyMedicine(){ return hasDrug()||hasMedicine();}
    boolean hasDrug();
    boolean hasMedicine();
    @Override
    default Roles role(){ return Roles.WITCH;}
}