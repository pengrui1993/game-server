package org.games.tx;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TxNtfLkSynTest {
    public static void main(String[] args) throws URISyntaxException, IOException {
        URL location = TxNtfLkSynTest.class.getProtectionDomain().getCodeSource().getLocation();
        String pkgName = TxNtfLkSynTest.class.getPackage().getName();
        String subDir = pkgName.replaceAll("\\.","/");
        File file = new File(location.getFile());
        if(!file.isDirectory())return;
        File pkg = new File(file,subDir);
        Class<?>[] classes = {TxAndNotifyWithLock.class,TxAndNotifyWithSync.class};
        Map<Class<?>,Object> ito = new HashMap<>();
        Optional.ofNullable(pkg.listFiles()).ifPresent(files->{
            for (File ces : files) {
                String string = ces.toString();
                String name = ces.getName();//monitor RequestService.class
                if(!string.endsWith(".class"))continue;
                String nameWithoutSuffix = name.replace(".class","");
                String pkgClass = pkgName+"."+nameWithoutSuffix;
                try {
                    Class<?> cc = TxNtfLkSynTest.class.getClassLoader().loadClass(pkgClass);
                    if(!cc.isInterface())continue;
                    for (Field field : cc.getFields()) {
                        if(field.getType()!=cc)continue;
//                        System.out.println(cc);
                        field.setAccessible(true);
                        ito.put(cc,field.get(null));
                    }
//                    System.out.println(cc);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
            for (Class<?> target : classes) {
                try {
                    Constructor<?> constructor = target.getConstructor();
                    Object host = constructor.newInstance();
                    for (Field field : target.getDeclaredFields()) {
                        Object o = ito.get(field.getType());
                        if(null == o)continue;
                        field.setAccessible(true);
                        try {
                            field.set(host,o);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Method onAction = target.getDeclaredMethod("onAction", Request.class);
                    onAction.setAccessible(true);
                    onAction.invoke(host, new Request() {});
                    System.out.println(onAction);
                } catch (NoSuchMethodException e) {
                    continue;
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
