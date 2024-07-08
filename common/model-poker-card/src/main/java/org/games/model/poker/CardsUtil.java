package org.games.model.poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum CardsUtil {
    ;
    private static List<Card> all;
    public static List<Card> allCards(){
        if(Objects.isNull(all)){
            all = Card.all();
        }
        return new ArrayList<>(all);
    }
}
