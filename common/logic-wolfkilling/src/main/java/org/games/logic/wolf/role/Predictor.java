package org.games.logic.wolf.role;

public interface Predictor extends Role{
    @Override
    default Roles role(){ return Roles.PREDICTOR;}
}
