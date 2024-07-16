package org.games.logic.xlch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mahjong {
    List<Player> players = new ArrayList<>();
    Player current;
    public Mahjong(){
        for(int i=0;i<4;i++){
            players.add(new Player(this));
        }
        current = players.get(0);
    }

    public void event(int type,int action,String sender,Object... params){
        Event from = Event.from(type);
        Player t = players.stream().filter(p -> Objects.equals(p.uid, sender)).findFirst().orElse(null);
        switch (from){
            case ACTION -> {
                players.forEach(p->p.action(action,t,params));
            }
            case MESSAGE -> {
                players.forEach(p->p.message(t,params));
            }
        }

    }
}
