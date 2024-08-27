package org.games.logic.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class EtcTest {
    public static void main(String[] args) {
        main3(args);
    }
    public static void main3(String[] args) {
        Runnable r = null;
        for(int i=0;i<3;i++){
            Runnable u = ()->{};
            System.out.println(r==u);//false,true,true
            r=u;
        }
        Consumer<String[]> c = EtcTest::main3;
        Consumer<String[]> c2 = EtcTest::main3;
        assert r != (Runnable)()->{};
        System.out.println(c==c2);//false
    }
    public static void main0(String[] args) throws Throwable{
        Robot robot = new Robot();
        Thread.sleep(3000);
        for (char c : "HELLO".toLowerCase().toCharArray()) {
            robot.keyPress(KeyEvent.VK_A+(c-'a'));//b
        }
//        robot.keyPress(KeyEvent.VK_E);
//        robot.keyPress(KeyEvent.VK_E);
        robot.keyPress(KeyEvent.VK_ENTER);
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
        System.out.println(System.in.read());
    }
    static void bits(int n){
        //System.out.println(-1>>>1);//bit shift
        int v = n;
        int d = 0;
        while(v!=0){
            v = v >>> 1;
            d++;
        }
        System.out.print(d+",");
        for(v=n,d=0;v!=0;){
            v = v >>> 1;
            d++;
        }
        System.out.print(d+" ");

    }
    static void print(boolean[][] arr){
        int row,col;
        for(row=0;row<arr.length;row++){
            for(col=0;col<arr[row].length;col++){
                System.out.print(arr[row][col]+" ");
            }
            System.out.println();
        }
    }
    static boolean[][] bits2(int bit){
        bit = Math.min(bit, 32);
        bit = bit<1?3:bit;//2->4 3->8 4->15
        int row = 2<<bit;
        int col = bit;
        boolean[][] arr = new boolean[row][col];
        for(row=0;row<(2<<bit);row++){//number to col row
            int number = row;
            for(col=0;col<bit;col++){
                arr[row][col] =  ((number>>>col)&0b01)!=0;
            }
        }
//        print(arr);
//        System.out.println();
//        for(row=0;row<arr.length;row++){//col row to number;
//            for(col=0;col<arr[row].length;col++){
//                int number= row;
//                arr[row][col] = ((number>>>col)&0b01)!=0;
//            }
//        }
//        print(arr);
        return arr;
    }
    void bits2(){
        //        bits(0xf);
        boolean[][] bs = bits2(3);
        for(int row=0;row<bs.length;row++){
            boolean[] bi = bs[row];
            boolean c1 = bi[0]&&bi[1]&&bi[2];
            boolean c2 = (!bi[0])||(!bi[1])||(!bi[2]);
            assert !c1==c2;
        }
    }
    static void bits3(boolean[][] arr){
        int row,col;
        for(row=0;row<arr.length;row++){
            for(col=0;col<arr[row].length;col++){
                int rowLen = arr[row].length;
                int val = row*rowLen+col;
                int t = val;
                int i=0;
                while(t!=0){
                    i++;
                    if(i==col){
                        arr[row][col]=0==(t&0x0fe);
                    }
                    t>>>=1;
                }
                System.out.print(val+" ");
            }
        }
    }
}
