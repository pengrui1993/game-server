package org.games.logic.wolf.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DeadInfo {
    enum Type{
        BOMB,WOLF,WITCH,VOTE,HUNTER,PROTECTOR,WITCH_WOLF
    }
    static class Item{
        public String user;
        public Type type;
        public int order;
        public int day;
        public Item(String user, Type type, int order, int day) {
            this.user = user;
            this.type = type;
            this.order = order;
            this.day = day;
        }
        @Override
        public String toString() {
            return "Item{" +
                    "user='" + user + '\'' +
                    ", type=" + type +
                    ", order=" + order +
                    ", day=" + day +
                    '}';
        }
    }
    int lastOrder = 0;
    final List<Item> items = new ArrayList<>();
    public void addDiedInfoByProtector(String user,int day){
        items.add(new Item(user,Type.PROTECTOR,lastOrder++,day));
    }
    public void addDiedInfoByWolfWitch(String user,int day){
        items.add(new Item(user,Type.WITCH_WOLF,lastOrder++,day));
    }
    public void addDiedInfoByWolf(String user,int day){
        items.add(new Item(user,Type.WOLF,lastOrder++,day));
    }
    public void addDiedInfoByWolfBomb(String user,int day){
        items.add(new Item(user,Type.BOMB,lastOrder++,day));
    }
    public void addDiedInfoByWitch(String user,int day){
        items.add(new Item(user,Type.WITCH,lastOrder++,day));
    }
    public void addDiedInfoByVote(String user,int day){
        items.add(new Item(user,Type.VOTE,lastOrder++,day));
    }
    public void addDiedInfoByHunter(String user,int day){
        items.add(new Item(user,Type.HUNTER,lastOrder++,day));
    }
    public void print(PrintStream out){
        PrintUtil.printDeathInfo(()->{
            out.println("died info:");
            if(items.isEmpty())return;
            for (Item item : items) {
                out.println(item);
            }
        });
    }
}
