package org.games.logic.xlch;

import org.games.model.mahjong.Tile;

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
    public void active(Player active) {
        if(active==this){
            care = Set.of(Action.HAND,Action.HU);
            handler = ()->{
                Tile tile = ctx.grab(this);
            };
            return;
        }
        if(ctx.isLeft(this,active)){
            care = Set.of(Action.PENG,Action.GANG,Action.HU,Action.CHI,Action.PASS);
            handler = ()->{
                if(activePlayer==this&&care.contains(curAction)){
                    switch (curAction){
                        case HAND -> {
                            //check...

                            ctx.next(this);
                        }
                        case NONE -> {}
                    }
                }else{

                }
            };
        }else if(ctx.isRight(this,active)){
            care = Set.of(Action.PENG,Action.GANG,Action.HU);
            handler = ()->{};
        }else{//other side
            care = Set.of(Action.PENG,Action.GANG,Action.HU);
            handler = ()->{

            };
        }
    }
    Runnable handler;
    Set<Action> care;
    Player activePlayer;
    Object[] params;
    Action curAction;
    public void action(int type,Player sender, Object... params){
        activePlayer = sender;
        curAction = Action.from(type);
        this.params = params;
        handler.run();
    }
    public void message(Player sender,Object... params){
        if(sender==this){

        }
    }

}
