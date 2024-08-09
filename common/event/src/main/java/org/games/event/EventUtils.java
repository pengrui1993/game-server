package org.games.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class EventUtils {
    static final PrintStream out = System.out;
    static final PrintStream err = System.err;
    static int count(String str,char c){
        int n =0;
        for (char c1 : str.toCharArray())if(c1==c)n++;
        return n;
    }
    public static void main(String[] args) throws IOException {
        List<Class<? extends F1>> load = load(C1.class, F1.class);
        out.println(load);
        out.println(classes);
    }
    interface F1{}
    interface F2 extends F1{}
    static class C1 implements F2{}

    static class C2 implements F2{}
    static class C3 implements F1{}

    /**
     *
     * @param clazz 搜索范围是该类的package及其子包
     * @param iface 需要是该接口的可实例化的类
     * @return 找到 clazz 类所在的package 的所有子包中 实现了 iface接口的 可以实例化的类 并返回
     * @param <T>
     * @throws IOException
     */
    public static <T> List<Class<? extends T>> load(Class<? extends T> clazz, Class<T> iface) throws IOException {
        List<Class<? extends T>> l = new ArrayList<>();
        final Function<Class<?>,Boolean> checker = (p)->{
            if(clazz.isInterface()&&p==clazz)return false;
            if(p.isInterface())return false;
            if(Modifier.isAbstract(p.getModifiers())) return false;
            Queue<Class<?>> queue = new LinkedList<>();
            queue.offer(p);
            Class<?> target;
            while(Objects.nonNull(target=queue.poll())){
                if(target.isInterface()){
                    if(target==iface)return true;
                    for (Class<?> is : target.getInterfaces()) queue.offer(is);
                }else{
                    for (Class<?> is : target.getInterfaces()) queue.offer(is);
                    if(Object.class!=target.getSuperclass()){
                        queue.offer(target.getSuperclass());
                    }
                }
            }
            return false;
        };
        if(checker.apply(clazz))l.add(clazz);
//        Class<SpringApplication> cc = SpringApplication.class;
//        print(App.class);//file:/Users/pengrui/gitee/game-server/bus/target/classes/
//        print(c);//file:/Users/pengrui/.m2/repository/org/springframework/boot/spring-boot/3.3.1/spring-boot-3.3.1.jar
        String pkg = clazz.getPackage().getName();
        String pkgDir = pkg.replaceAll("\\.","/");
        int dirCount = count(pkgDir,'/')+1;
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        Consumer<JarEntry> jarHandler = (jarEntry)->{
            String name = jarEntry.getName();
            if(name.endsWith(".class")
                    &&name.startsWith(pkgDir)
                    &&dirCount==count(name,'/')
            ){
                try {
                    String cz = name.replaceAll("/", ".").replaceAll(".class","");
                    Class<?> target = Class.forName(cz);
                    if(checker.apply(target)) l.add((Class<? extends T>)target);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace(err);
                }
            }
        };
        String protocol = location.getProtocol();
        if(protocol.equals("file")){
            if(location.toString().endsWith(".jar")){
                JarFile jarFile = new JarFile(location.getFile());
                Enumeration<JarEntry> entries = jarFile.entries();
//                Pattern compile = Pattern.compile("(\\$\\d)");
                while(entries.hasMoreElements()){
                    JarEntry jarEntry = entries.nextElement();
                    jarHandler.accept(jarEntry);
                }
                jarFile.close();
            }else{
                File file = new File(location.getFile(),pkgDir);
//                System.out.println(file);
                Optional.ofNullable(file.listFiles()).ifPresent(fs->{
                    for (File f : fs) {
                        if(f.getName().endsWith(".class")){
                            String cz = pkg+"."+f.getName().replaceAll("/", ".").replaceAll(".class","");
                            Class<?> target;
                            try {
                                target = Class.forName(cz);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace(System.err);
                                System.exit(-1);
                                return;
                            } catch (Throwable t){
                                t.printStackTrace(System.err);
                                System.exit(-2);
                                return;
                            }
                            if(checker.apply(target)) l.add((Class<? extends T>)target);
                        }
                    }
                });

            }
        }else if(protocol.equals("jar")){
            JarURLConnection conn = (JarURLConnection)location.openConnection();
            Enumeration<JarEntry> entries = conn.getJarFile().entries();
            while(entries.hasMoreElements()){
                JarEntry jarEntry = entries.nextElement();
                jarHandler.accept(jarEntry);
            }
            out.println("exit with jar");
        }else{
            out.println("unknown protocol:"+protocol);
        }
        return l;
    }
    public static final List<Class<? extends Event>> classes;
    public final static Gson g;
    static{
        List<Class<? extends Event>> classes1;
        try {
            classes1 = load(Event.class, Event.class);
        } catch (IOException e) {
            System.exit(-1);
            classes1 = Collections.emptyList();
        }
        classes = classes1;
        GsonBuilder gsonBuilder = new GsonBuilder();
        EventSerializer se = new EventSerializer();
        EventDeserializer ed = new EventDeserializer();
        for (Class<? extends Event> aClass : classes1) {
            gsonBuilder.registerTypeAdapter(aClass,se);
        }
        gsonBuilder.registerTypeAdapter(AbstractEvent.class,ed);
        g= gsonBuilder.create();
    }

}
