package org.games.logic.wolf.role;



public interface Wolf extends Role{
    @Override
    default Roles role(){ return Roles.WOLF;}
}