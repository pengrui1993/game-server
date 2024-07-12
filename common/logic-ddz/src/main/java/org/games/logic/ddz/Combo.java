package org.games.logic.ddz;

import org.games.model.poker.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Combo {
    public static final Combo ZERO = new Combo(Collections.emptyList());
    public final List<Card> cards;
    private ComboType type;
    private final List<Card> primary = new ArrayList<>();
    public boolean isValid(){
        return type!=ComboType.UNKNOWN;
    }
    public Combo(List<Card> c) {
        ArrayList<Card> cards = new ArrayList<>(c);
        cards.sort(Card.cmp0());
        parse(this);
        this.cards = Collections.unmodifiableList(cards);
    }
    private static void parse(Combo c){
        switch (c.cards.size()){
            case 1->{
                c.type = ComboType.SINGLE;
                c.primary.add(c.cards.get(0));
            }
            case 2->{
                if(0==Card.valueCmp(c.cards.get(0),c.cards.get(1))){
                    c.type = ComboType.DOUBLE;
                    c.primary.add(c.cards.get(0));
                }else{
                    if(
                        Card.valueCmp(c.cards.get(0),Card.RED_JOKER)==0
                        &&Card.valueCmp(Card.BLACK_JOKER,c.cards.get(1))==0
                    ){
                        c.type = ComboType.DOUBLE_JOKER;
                        c.primary.add(c.cards.get(0));
                    }else{
                        c.type = ComboType.UNKNOWN;
                    }
                }
            }
            case 3->{
                boolean ok = true;
                for(int i=0;i<c.cards.size()-1;i++){
                    Card l = c.cards.get(i);
                    Card r = c.cards.get(i+1);
                    if(0!=Card.valueCmp(l,r)){
                        ok=false;
                        break;
                    }
                }
                if(ok){
                    c.type = ComboType.THIRD;
                    c.primary.add(c.cards.get(0));
                }else{
                    c.type = ComboType.THIRD;
                }
            }
            case 4->{c.type = ComboType.UNKNOWN;}
            case 5->{c.type = ComboType.UNKNOWN;}
            case 6->{c.type = ComboType.UNKNOWN;}
            case 7->{c.type = ComboType.UNKNOWN;}
            case 8->{c.type = ComboType.UNKNOWN;}
            case 9->{c.type = ComboType.UNKNOWN;}
            //TODO
        }
        ;
    }
    public static Combo from(List<Card> cards){
        return new Combo(cards);
    }
    public int cmp(Combo other){
        boolean typeNoEqual = type!=other.type;
        if (typeNoEqual) {
            boolean difSize = other.cards.size() != cards.size();
            if (difSize) {
                boolean areBoom = other.type.boomValue > 0 && type.boomValue > 0;
                if (areBoom) return other.type.boomValue - type.boomValue;
                return type.boomValue <= 0 ? -1 : 1;
            }
        }
        int v =0;
        for(int i=0;i<primary.size();i++){
            Card l = primary.get(i);
            Card r = other.primary.get(i);
            int idx = i;
            Comparator<Card> c = (o1, o2) -> idx==0
                    ?Card.valueCmp(o1,o2)
                    :Card.valueCmpWithNull(o1,o2);
            if(0!=(v=c.compare(l,r)))return v;
        }
        return v;
    }
    public boolean isBiggerThen(Combo other){
        if(this==other)return false;
        if(null==other)return false;
        if(other==ZERO)return true;
        return isValid()&&other.isValid()&&cmp(other)>0;
    }
}
