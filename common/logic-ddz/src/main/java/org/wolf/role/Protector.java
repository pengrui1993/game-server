package org.wolf.role;

public interface Protector extends Role{
    @Override
    default Roles role(){ return Roles.PROTECTOR;}
}