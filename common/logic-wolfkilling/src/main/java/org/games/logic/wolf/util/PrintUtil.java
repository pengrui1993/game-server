package org.games.logic.wolf.util;

public class PrintUtil {

    private static void print(Runnable r){
        r.run();
    }
    public static void printDeathInfo(Runnable r){
        print(r);
    }
}
