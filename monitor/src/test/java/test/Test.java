package test;

import cn.hutool.Hutool;

public class Test {
    public static void main(String[] args) {
//        Hutool.printAllUtils();
        for (Class<?> allUtil : Hutool.getAllUtils()) {
            System.out.println(allUtil);
        }
    }
}
