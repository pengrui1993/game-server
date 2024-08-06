package org.games.logic.wolf.role;

public interface Farmer extends Role{

    @Override
    default Roles role(){ return Roles.FARMER;}
}
