package org.games.model.mahjong;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/*
 筒子 条子 万字 中发白 东南西北
 */
public class Tile {
    private final byte value;
    //                           val s   tp
    static final byte BIT = 0b0_0000_0___00;
    static final int TYPE_BITS = 2;
    static final int SIMPLE_TYPE_OFFSET = 0;
    static final int SIMPLE_TYPE_MASK = 0b011;
    //special zhong fa bai feng
    static final int SPECIAL_BITS = 1;
    static final int SPECIAL_OFFSET = TYPE_BITS;
    static final int SPECIAL_MASK = 0b0100;
    static final int TYPE_MASK = SIMPLE_TYPE_MASK|SPECIAL_MASK;
    static final int TYPE_OFFSET = SIMPLE_TYPE_OFFSET;
    static final int VALUE_BITS = 4;
    static final int VALUE_OFFSET = SPECIAL_OFFSET+SPECIAL_BITS;
    static final int VALUE_MASK = 0b01111000;
    static final int TYPE_TONG = 0b001;
    static final int TYPE_TIAO = 0b010;
    static final int TYPE_WAN  = 0b011;
    static final int TYPE_HUA  = 0b101;
    static final int TYPE_ZFBF = 0b110;
    public static final int
              V1 = 1, V2 = 2 , V3 = 3 , V4 = 4 , V5 = 5 , V6 = 6 , V7 = 7 , V8 = 8 , V9 = 9
            //风字 值 east,south,west,north            中发白 zhong fa bai
            , VE = 1 , VS = 2 , VW = 3 , VN = 4     , VZ = 5 , VF = 6 , VB = 7
            //梅兰竹菊春夏秋冬
            ,VM = 1,VL = 2, VZU = 3, VJ = 4,VC = 5,VX = 6, VQ = 7, VD = 8



    ;

//    public static final int T

    private Tile(int type,int val) {
        this.value = (byte)((val<<VALUE_OFFSET)|(type<<TYPE_OFFSET));

    }
    public boolean isZhongFaBaiFeng(){
        return ((value&TYPE_MASK)>>TYPE_OFFSET)== TYPE_ZFBF;
    }
    public boolean isZhong(){
        return isZhongFaBaiFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VZ;
    }
    public boolean isFa(){
        return isZhongFaBaiFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VF;
    }
    public boolean isBai(){
        return isZhongFaBaiFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VB;
    }
    public boolean isEast(){
        return isFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VE;
    }
    public boolean isSouth(){
        return isFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VS;
    }
    public boolean isWest(){
        return isFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VW;
    }
    public boolean isNorth(){
        return isFeng()&&((value&VALUE_MASK)>>VALUE_OFFSET)==VN;
    }
    public boolean isFeng(){
        int val;
        return isZhongFaBaiFeng()&&((val=((value&VALUE_MASK)>>VALUE_OFFSET))>=VE)&&val<=VN;
    }
    public boolean isTong(){
        return (value&TYPE_MASK)==TYPE_TONG;
    }
    public boolean isTiao(){
        return (value&TYPE_MASK)==TYPE_TIAO;
    }
    public boolean isWan(){
        return (value&TYPE_MASK)==TYPE_WAN;
    }
    public boolean isTongTiaoWan(){
        return (SPECIAL_MASK&value)==0;
    }
    /*
        筒子 条子 万子
        中发白
        东南西北
     */
    public static final Tile
             _1TONG=new Tile(TYPE_TONG,V1),_2TONG=new Tile(TYPE_TONG,V2),_3TONG=new Tile(TYPE_TONG,V3),_4TONG=new Tile(TYPE_TONG,V4),_5TONG=new Tile(TYPE_TONG,V5),_6TONG=new Tile(TYPE_TONG,V6),_7TONG=new Tile(TYPE_TONG,V7),_8TONG=new Tile(TYPE_TONG,V8),_9TONG=new Tile(TYPE_TONG,V9)
            ,_1TIAO=new Tile(TYPE_TIAO,V1),_2TIAO=new Tile(TYPE_TIAO,V2),_3TIAO=new Tile(TYPE_TIAO,V3),_4TIAO=new Tile(TYPE_TIAO,V4),_5TIAO=new Tile(TYPE_TIAO,V5),_6TIAO=new Tile(TYPE_TIAO,V6),_7TIAO=new Tile(TYPE_TIAO,V7),_8TIAO=new Tile(TYPE_TIAO,V8),_9TIAO=new Tile(TYPE_TIAO,V9)
            ,_1WAN=new Tile(TYPE_WAN,V1),_2WAN=new Tile(TYPE_WAN,V2),_3WAN=new Tile(TYPE_WAN,V3),_4WAN=new Tile(TYPE_WAN,V4),_5WAN=new Tile(TYPE_WAN,V5),_6WAN=new Tile(TYPE_WAN,V6),_7WAN=new Tile(TYPE_WAN,V7),_8WAN=new Tile(TYPE_WAN,V8),_9WAN=new Tile(TYPE_WAN,V9)
            ,ZHONG=new Tile(TYPE_ZFBF,VZ),FA=new Tile(TYPE_ZFBF,VF),BAI=new Tile(TYPE_ZFBF,VB),EAST=new Tile(TYPE_ZFBF,VE),SOUTH=new Tile(TYPE_ZFBF,VS),WEST=new Tile(TYPE_ZFBF,VW),NORTH=new Tile(TYPE_ZFBF,VN)
            ,MEI=new Tile(TYPE_HUA,VM),LAN=new Tile(TYPE_HUA,VL),ZU=new Tile(TYPE_HUA,VZU),JU=new Tile(TYPE_HUA,VJ),SPRING=new Tile(TYPE_HUA,VC),SUMMER=new Tile(TYPE_HUA,VX),AUTUMN=new Tile(TYPE_HUA,VQ),WINTER=new Tile(TYPE_HUA,VD)
            //花牌 https://www.emojiall.com/zh-hans/node/72
            ;
    public static void genAll() {
            for (char c : "Ttw".toCharArray()) {
                for(int j=1;j<=9;j++){
                    String name = "tong tiao wan";
                    String val = "1 2 3 4 5...";
                    String num = "1 2 3 4";
                    String type = "TYPE_TONG";
                    String vn = "V1 V2 ...";
                    name = switch (c) {
                        case 'T' -> "TONG";
                        case 't' -> "TIAO";
                        default -> "WAN";
                    };
                    type = switch (c) {
                        case 'T' -> "TYPE_TONG";
                        case 't' -> "TYPE_TIAO";
                        default -> "TYPE_WAN";
                    };
                    val = ""+j;
                    vn = "V"+j;
                    String title = "_"+val+name;
                    String col = """
                            ,$1=new Tile($2,$3) """;
                    System.out.print(col.replace("$1",title)
                            .replace("$2",type)
                            .replace("$3",vn)
                    );
                }
                System.out.println();
            }
            for (char c : "ZFB".toCharArray()) {
                String val = "ZHONG FA BAI";
                String type = "TYPE_ZFB";
                String vn = "VZ VF VB";
                switch (c){
                    case 'Z':{
                        vn = "VZ";
                        val = "ZHONG";
                    }break;
                    case 'F':{
                        vn = "VF";
                        val = "FA";
                    }break;
                    default:{
                        vn = "VB";
                        val = "BAI";
                    }
                }
                String title = val;
                String col = """
                            ,$1=new Tile($2,$3) """;
                System.out.print(col.replace("$1",title)
                        .replace("$2",type)
                        .replace("$3",vn)
                );
            }
        for (char c : "ESWN".toCharArray()) {
            String val = "ZHONG FA BAI";
            String type = "TYPE_ZFB";
            String vn = "VZ VF VB";
            switch (c){
                case 'E':{
                    vn = "VE";
                    val = "EAST";
                }break;
                case 'S':{
                    vn = "VS";
                    val = "SOUTH";
                }break;
                case 'W':{
                    vn = "VW";
                    val = "WEST";
                }break;
                default:{
                    vn = "VN";
                    val = "NORTH";
                }
            }
            String title = val;
            String col = """
                            ,$1=new Tile($2,$3) """;
            System.out.print(col.replace("$1",title)
                    .replace("$2",type)
                    .replace("$3",vn)
            );
        }
        System.out.println();
        for (char c : "MLZJCXQD".toCharArray()) {
            String val = "MEI LAN ZU JU SPRING SUMMER AUTUMN WINTER";
            String type = "TYPE_HUA";
            String vn = "VZ VF VB";
            switch (c){
                case 'M':{vn = "VM"; val = "MEI";}break;
                case 'L':{vn = "VL";val = "LAN";}break;
                case 'Z':{vn = "VZU";val = "ZU";}break;
                case 'J':{vn = "VJ";val = "JU";}break;
                case 'C':{vn = "VC";val = "SPRING";}break;
                case 'X':{vn = "VX";val = "SUMMER";}break;
                case 'Q':{vn = "VQ";val = "AUTUMN";}break;
                default:{
                    vn = "VD";
                    val = "WINTER";
                }
            }
            String title = val;
            String col = """
                            ,$1=new Tile($2,$3) """;
            System.out.print(col.replace("$1",title)
                    .replace("$2",type)
                    .replace("$3",vn)
            );

        }
    }

    public static void gen1(String[] args) {
        String val = """
                public static final int V$1 = $2;""";
        for(int i=1;i<10;i++)
            System.out.println(val.replace("$1",""+i).replace("$2",""+i));
    }
    static void show(){
        int i=0;
        int count = 0;
        for (Tile tile : getAll()) {
            i++;
            count ++;
            System.out.print(tile);
            if(i==9){
                System.out.println();
                i=0;
            }
        }
        System.out.println();
        System.out.println(count);
    }
    public static void main(String[] args) {
//        genAll();
        show();
    }
    public static List<Tile> getAll() {
        List<Tile> list = new ArrayList<>();
        for (Field f : Tile.class.getFields()) {
            if(Modifier.isStatic(f.getModifiers())&&f.getType()==Tile.class){
                f.setAccessible(true);
                Tile c;
                try {
                    c = (Tile)f.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                list.add(c);
            }
        }
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if(null==obj)return false;
        if(obj==this)return true;
        if(obj instanceof Tile c){
            return c.value == value;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        final int val = (value & VALUE_MASK) >> VALUE_OFFSET;
        switch ((value&TYPE_MASK)>>TYPE_OFFSET){
            case TYPE_TONG:{//🀙🀚🀛🀜🀝🀞🀟🀠🀡
                final char c1 = '\uD83C';
                final char c2 = '\uDC19'-1;
                return String.valueOf(c1) + (char) (c2 + val);
            }
            case TYPE_WAN:{//🀇🀈🀉🀊🀋🀌🀍🀎🀏
                final char c1 = '\uD83C';
                final char c2 = '\uDC07'-1;
                return String.valueOf(c1) + (char) (c2 + val);
            }
            case TYPE_TIAO:{//🀐🀑🀒🀓🀔🀕🀖🀗🀘
                final char c1 = '\uD83C';
                final char c2 = '\uDC10'-1;
                return String.valueOf(c1) + (char) (c2 + val);
            }
            case TYPE_HUA:{//🀢🀣🀤🀥🀦🀧🀨🀩
                final char c1 = '\uD83C';
                final char c2 = '\uDC22'-1;
                return String.valueOf(c1) + (char) (c2 + val);
            }
            case TYPE_ZFBF:{//🀀🀁🀂🀃🀄🀅🀆
                final char c1 = '\uD83C';
                final char c2 = '\uDC00'-1;
                return String.valueOf(c1) + (char) (c2 + val);
            }
            default:return "\uD83C\uDC2B";//🀫
        }
    }
}
