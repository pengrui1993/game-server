package org.games.logic.xlch;

import org.games.model.mahjong.Tile;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Mahjong {
    static final PrintStream out = System.out;
    List<Player> players = new ArrayList<>();
    Player current;
    public Mahjong(){
        for(int i=0;i<4;i++){
            players.add(new Player(this));
        }
        current = players.get(0);
        players.forEach(p-> p.active(current));
    }
    public void next(Player p){
        if(p!=current)
            throw new RuntimeException("invalid next operator");
        int index=0;
        for(int i=0;i<players.size();i++){
            if(players.get(i)==p){
                index = i;
                break;
            }
        }
        current = players.get((index+1)%players.size());
        players.forEach(pl->pl.active(current));
    }
    public void event(int type,int action,String sender,Object... params){
        Player t = players.stream().filter(p -> Objects.equals(p.uid, sender)).findFirst().orElse(null);
        switch (Event.from(type)){
            case ACTION -> {
                players.forEach(p->p.action(action,t,params));
            }
            case MESSAGE -> {
                players.forEach(p->p.message(t,params));
            }
        }
    }

    /**
     * p is left ref that
     * @param that
     * @param p
     * @return
     */
    public boolean isLeft(Player that, Player p) {
        for(int i=0;i<players.size()-1;i++){
            if(p==players.get(i)&&that==players.get(i+1))return true;
        }
        return p == players.get(players.size() - 1) && that == players.get(0);
    }

    public boolean isRight(Player that, Player p) {
        return isLeft(p,that);
    }
    List<Tile> wall = new ArrayList<>();
    Tile last;
    public Tile grab(Player p) {
        if(p!=current)
            throw new RuntimeException("invalid grab operator");
        return last = wall.remove(0);
    }
}
