package org.games.logic.xlch;

import java.util.Set;

public class Player {
    private final Mahjong ctx;
    public String uid;
    public Player(Mahjong mahjong) {
        this.ctx = mahjong;
        care = Set.of(Action.NONE);
        handler= ()->{

        };
    }
    Runnable handler;
    Set<Action> care;
    Object[] params;
    public void action(int type,Player sender, Object... params){
        if(sender==this){
            Action t = Action.from(type);
            if(care.contains(t)){
                this.params = params;
                handler.run();
            }
        }else{

        }
    }
    public void message(Player sender,Object... params){
        if(sender==this){

        }
    }
}
