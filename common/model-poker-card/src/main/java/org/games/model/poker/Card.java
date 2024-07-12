package org.games.model.poker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Card {
    /*
        highest bit:7
        lowest bit:0
        是否是赖子 bit:7
     */
    //                           sr    val     type
//    static final byte BITS = 0b00____0000____00;
    static final int TEST_VALUE = 0b00_1111_00;
    static final int TEST_START = 0b10_0000_00;
    static final int TEST_JOKER = 0b01_0000_00;
    static final int TEST_TYPE = 0b00_0000_11;

    private Card(boolean star,boolean joker,int v,int type) {
        if(star){
            code = (byte)(0b11111111&STAR_MASK);
        }else{
            if(joker){
                code = (byte)((0b11111111&JOKER_MASK)|((v<<VALUE_BIT_OFFSET)&VALUE_MASK));
            }else{
                code = (byte)(((v<<VALUE_BIT_OFFSET)&VALUE_MASK)|((type<<TYPE_BIT_OFFSET)&TYPE_MASK));
            }
        }
    }

    static int bit(int bits,int shift){
        if(bits<=0)return 0;
        if(shift<0)return 0;
        int value=0;
        for(int i=0;i<bits;i++)value|=(0x1<<i);
        value<<=shift;
        return value;
    }
    static final int TYPE_BITS = 2;
    static final int TYPE_BIT_OFFSET = 0;
    static final int TYPE_MASK = bit(TYPE_BITS,TYPE_BIT_OFFSET);
    static final int VALUE_BITS = 4;
    static final int VALUE_BIT_OFFSET = TYPE_BIT_OFFSET+TYPE_BITS;
    static final int VALUE_MASK = bit(VALUE_BITS,VALUE_BIT_OFFSET);
    static final int JOKER_BITS = 1;
    static final int JOKER_BIT_OFFSET = VALUE_BIT_OFFSET+VALUE_BITS;
    static final int JOKER_MASK = bit(JOKER_BITS, JOKER_BIT_OFFSET);
    static final int STAR_BITS = 1;
    static final int STAR_BIT_OFFSET = JOKER_BIT_OFFSET + JOKER_BITS;
    static final int STAR_MASK = bit(STAR_BITS, STAR_BIT_OFFSET);
    //即梅花(Club)为C，方块(Diamond)为D，红心(Hearts)为H，黑桃(Spade)为S。
    //type club
    public static final int TC = 0b11;
    public static final int TH = 0b10;
    public static final int TS = 0b01;
    public static final int TD = 0b00;

    //black rocket
    public static final int VBJ = 1;
    //red rocket
    public static final int VRJ = 2;
    public static final int V3=3;
    public static final int V4=4;
    public static final int V5=5;
    public static final int V6=6;
    public static final int V7=7;
    public static final int V8=8;
    public static final int V9=9;
    public static final int V10=10;
    public static final int VJ=11;
    public static final int VQ=12;
    public static final int VK=13;
    public static final int VA=14;
    public static final int V2=15;
    public final byte code;

    public boolean isSpecialType(){
        return isJoker()||isStar();
    }
    public boolean isNormalType(){
        return !isSpecialType();
    }
    public boolean isClub(){
        return isNormalType()&&(TYPE_MASK& code)==TC;
    }
    public boolean isHearts(){
        return isNormalType()&&(TYPE_MASK& code)==TH;
    }
    public boolean isSpade(){
        return isNormalType()&&(TYPE_MASK& code)==TS;
    }
    public boolean isDiamond(){
        return isNormalType()&&(TYPE_MASK& code)==TD;
    }
    public boolean isStar(){
        return 0!=(STAR_MASK & code);
    }
    public boolean isJoker(){
        return 0!=(JOKER_MASK& code);
    }
    public boolean isRedJoker(){
        return isJoker()&&((VALUE_MASK& code)>>VALUE_BIT_OFFSET)==VRJ;
    }
    static final String CLUB = "♠";
    static final String HEARTS = "♥";
    static final String SPADE = "♣";
    static final String DIAMOND = "♦";
    static final String STAR_ = "*";
    static final String DISPLAY_CLUB = "♠️";
    static final String DISPLAY_HEARTS = "♥️";
    static final String DISPLAY_SPADE = "♣️";
    static final String DISPLAY_DIAMOND = "♦️";
    static final String DISPLAY_START = "🌟";
    static final String DISPLAY_RJ = "🃏";
    static final String DISPLAY_BJ = "\uD83C\uDCDF";//🃟

    public static void main2(String[] args) {
        System.out.println((0b10000000_00000000_00000000_00000000));
        System.out.println(TYPE_MASK==TEST_TYPE);
        System.out.println(VALUE_MASK==TEST_VALUE);
        System.out.println(JOKER_MASK == TEST_JOKER);
        System.out.println(STAR_MASK ==TEST_START);
        System.out.println(CLUB);
        System.out.println(HEARTS);
        System.out.println(SPADE);
        System.out.println(DIAMOND);
        System.out.println(DISPLAY_CLUB);
        System.out.println(DISPLAY_HEARTS);
        System.out.println(DISPLAY_SPADE);
        System.out.println(DISPLAY_DIAMOND);
    }

    public static int valueCmpWithNull(Card l,Card r){
        if(l==r)return 0;
        if(null==l)return 1;
        if(null==r)return -1;
        return valueCmp(l,r);
    }
    public boolean sameVal(int val){
        return val ==((code &VALUE_MASK)>>VALUE_BIT_OFFSET);
    }
    public static int valueCmp(Card l,Card r){
        return l.code -r.code;
    }
    public static int typeCmp(Card l,Card r){
        if(l.isStar())return 1;
        if(r.isStar())return -1;
        if(l.isJoker())return l.code -r.code;
        return l.code &TYPE_MASK-r.code &TYPE_MASK;
    }
    public static Comparator<Card> cmp0(){
        final Comparator<Card> cmp = (o1, o2) -> {
            int v = -Card.valueCmp(o1, o2);
            return v==0?-Card.typeCmp(o1,o2):v;
        };
        return cmp;
    }
    public static Card from(char c,int type){
        switch (c){
            case '2': return CLUB_2;
        }

        return null;
    }
    public static void main1(String[] args) {
        for(int i=3;i<=15;i++){
            String s = """
                    public static final int V$1=$2;""";
            String v = i+"";
            String d = "";
            switch(i/10){
                case 0:{d=v;}break;
                default:{
                    switch(i%10){
                        case 1:{d="J";}break;
                        case 2:{d="Q";}break;
                        case 3:{d="K";}break;
                        case 4:{d="A";}break;
                        default:{d="2";}break;
                    }
                }
                String line = s.replace("$1",v).replace("$2",""+i);
                System.out.println(line);

            }
        }
    }
    @Override
    public int hashCode() {
        return code;
    }
    @Override
    public boolean equals(Object obj) {
        if(null==obj)return false;
        if(obj==this)return true;
        if(obj instanceof Card c){
            return c.code == code;
        }
        return false;
    }

    @Override
    public String toString() {
        if(isStar()){
            return DISPLAY_START+"#";
        }else{
            if(isJoker()){
                if(isRedJoker()){
                    return DISPLAY_RJ+"#";
                }else{
                    return DISPLAY_BJ+" #";
                }
            }else{
                String t,v;
                final int val = (code &VALUE_MASK)>>VALUE_BIT_OFFSET;
                if(val<=10){
                    v =val+"";
                }else{
                    v = switch (val % 10) {
                        case 1 -> "J";
                        case 2 -> "Q";
                        case 3 -> "K";
                        case 4 -> "A";
                        case 5 -> "2";
                        default -> "?";
                    };
                }
                final int type = (code &TYPE_MASK)>>TYPE_BIT_OFFSET;
                t = switch (type) {
                    case TC -> DISPLAY_CLUB;
                    case TH -> DISPLAY_HEARTS;
                    case TS -> DISPLAY_SPADE;
                    case TD -> DISPLAY_DIAMOND;
                    default -> "#";
                };
                return t+v;
            }
        }
    }

    public static void gen(String[] args) {
        for(int row=0;row<4;row++){
            for(int val=3;val<=V2;val++){
                String template = """
                        ,$1$2 = new Card(false,false,V$3,$4)""";
                String type = "C H S D";
                String value = "3 4 ... 10 J Q K A 2";
                String tv = "T"+type;
                switch (row){
                    case 0:{
                        type = "CLUB_";
                        tv = "TC";
                    }break;
                    case 1:{
                        type = "HEARTS_";
                        tv = "TH";
                    }break;
                    case 2:{
                        type = "SPADE_";
                        tv = "TS";
                    }break;
                    default:{
                        type = "DIAMOND_";
                        tv = "TD";
                    }break;
                }
                switch (val/10){
                    case 0:{
                        value=""+val;
                    }break;
                    default:{
                        boolean c = false;
                        value = switch (val) {
                            case V10 -> "10";
                            case VJ -> "J";
                            case VQ -> "Q";
                            case VK -> "K";
                            case VA -> "A";
                            case V2 -> "2";
                            default -> {
                                c = true;
                                yield value;
                            }
                        };
                        if(c)
                            continue;
                    }
                }
                String col = template.replace("$1",type)
                        .replace("$2",value)
                        .replace("$3",value)
                        .replace("$4",tv)
                        ;
                System.out.print(col);
            }
            System.out.println();
        }
    }
//                 System.out.println(CLUB);
//        System.out.println(HEARTS);
//        System.out.println(SPADE);
//        System.out.println(DIAMOND);
    public static final Card
            RED_JOKER= new Card(false,true, VRJ,0)
            ,BLACK_JOKER = new Card(false,true,VBJ,0)
            , STAR = new Card(true,false,0,0)
            ,CLUB_3 = new Card(false,false,V3,TC),CLUB_4 = new Card(false,false,V4,TC),CLUB_5 = new Card(false,false,V5,TC),CLUB_6 = new Card(false,false,V6,TC),CLUB_7 = new Card(false,false,V7,TC),CLUB_8 = new Card(false,false,V8,TC),CLUB_9 = new Card(false,false,V9,TC),CLUB_10 = new Card(false,false,V10,TC),CLUB_J = new Card(false,false,VJ,TC),CLUB_Q = new Card(false,false,VQ,TC),CLUB_K = new Card(false,false,VK,TC),CLUB_A = new Card(false,false,VA,TC),CLUB_2 = new Card(false,false,V2,TC)
            ,HEARTS_3 = new Card(false,false,V3,TH),HEARTS_4 = new Card(false,false,V4,TH),HEARTS_5 = new Card(false,false,V5,TH),HEARTS_6 = new Card(false,false,V6,TH),HEARTS_7 = new Card(false,false,V7,TH),HEARTS_8 = new Card(false,false,V8,TH),HEARTS_9 = new Card(false,false,V9,TH),HEARTS_10 = new Card(false,false,V10,TH),HEARTS_J = new Card(false,false,VJ,TH),HEARTS_Q = new Card(false,false,VQ,TH),HEARTS_K = new Card(false,false,VK,TH),HEARTS_A = new Card(false,false,VA,TH),HEARTS_2 = new Card(false,false,V2,TH)
            ,SPADE_3 = new Card(false,false,V3,TS),SPADE_4 = new Card(false,false,V4,TS),SPADE_5 = new Card(false,false,V5,TS),SPADE_6 = new Card(false,false,V6,TS),SPADE_7 = new Card(false,false,V7,TS),SPADE_8 = new Card(false,false,V8,TS),SPADE_9 = new Card(false,false,V9,TS),SPADE_10 = new Card(false,false,V10,TS),SPADE_J = new Card(false,false,VJ,TS),SPADE_Q = new Card(false,false,VQ,TS),SPADE_K = new Card(false,false,VK,TS),SPADE_A = new Card(false,false,VA,TS),SPADE_2 = new Card(false,false,V2,TS)
            ,DIAMOND_3 = new Card(false,false,V3,TD),DIAMOND_4 = new Card(false,false,V4,TD),DIAMOND_5 = new Card(false,false,V5,TD),DIAMOND_6 = new Card(false,false,V6,TD),DIAMOND_7 = new Card(false,false,V7,TD),DIAMOND_8 = new Card(false,false,V8,TD),DIAMOND_9 = new Card(false,false,V9,TD),DIAMOND_10 = new Card(false,false,V10,TD),DIAMOND_J = new Card(false,false,VJ,TD),DIAMOND_Q = new Card(false,false,VQ,TD),DIAMOND_K = new Card(false,false,VK,TD),DIAMOND_A = new Card(false,false,VA,TD),DIAMOND_2 = new Card(false,false,V2,TD)
            ;
    static List<Card> all() {
        List<Card> list = new ArrayList<>();
        for (Field f : Card.class.getFields()) {
            if(Modifier.isStatic(f.getModifiers())&&f.getType()==Card.class){
                f.setAccessible(true);
                Card c;
                try {
                    c = (Card)f.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                list.add(c);
            }
        }
        return list;
    }
    public static void display() {
        int count =0 ;
        int cc = 0;
        for (Card card : all()) {
            cc++;
            count++;
            System.out.print(card+" ");
            if(cc>9){
                System.out.println();
                cc=0;
            }
        }
        System.out.println();
        System.out.println(count);
    }
    public static void main(String[] args) throws IllegalAccessException {
//        display();
        System.out.println(all());
    }
}
