package org.games.model.poker;

import java.util.*;
import java.util.stream.Collectors;

public enum CardsUtil {
    ;
    private static List<Card> all;

    private static Map<Byte,Card> map;
    public static List<Card> allCards(){
        if(Objects.isNull(all)){
            all = Collections.unmodifiableList(Card.all());
        }
        return new ArrayList<>(all);
    }
    public static List<Card> allCardsWithoutStart(){
        ArrayList<Card> arr = new ArrayList<>(allCards());
        arr.remove(Card.STAR);
        return arr;
    }
    public static Card by(byte b){
        if(Objects.isNull(map)){
            map = Collections.unmodifiableMap(allCards()
                    .stream()
                    .collect(Collectors.toMap(f->f.code, f->f)));
        }
        return map.get(b);
    }
    public static List<Card> byList(List<Byte> bytes){
        return bytes.stream().map(CardsUtil::by).toList();
    }
}
