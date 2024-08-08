package test;

import cn.hutool.Hutool;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class Test {
    public static void main(String[] args) {
//        Hutool.printAllUtils();
        for (Class<?> allUtil : Hutool.getAllUtils()) {
            System.out.println(allUtil);
        }

        Enhancer e = new Enhancer();
        e.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> {
            proxy.invoke(obj,args1);
//            method.invoke(obj,args1);
            return method.invoke(obj,args1);
        });
    }
}
