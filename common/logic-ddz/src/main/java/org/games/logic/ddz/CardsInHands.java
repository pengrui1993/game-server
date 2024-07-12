package org.games.logic.ddz;

import org.games.model.poker.Card;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CardsInHands {
    public final String userId;
    public final List<Card> cards= new ArrayList<>();
    public CardsInHands(String userId) {
        this.userId = userId;
    }
    public boolean empty(){
        return cards.isEmpty();
    }
    public void add(Card card) {
        assert null!=card;
        cards.add(card);
    }
    static final PrintStream out = System.out;
    public CardsInHands sort() {
        cards.sort(Card.cmp0());
        return this;
    }

    public void print() {
        out.println(userId+":"+cards);
    }
    public void queryByCardVal(int v,List<Card> l){
        if(null==l)return;
        for (Card card : cards) {
            if(card.sameVal(v))l.add(card);
        }
    }
    public void remove(List<Card> c) {
        if(null==c||c.isEmpty()||cards.isEmpty())return;
//        int ss = c.size();
//        int ds = cards.size()-ss;
        c.forEach(cards::remove);
//        assert ds == cards.size();
    }
}
