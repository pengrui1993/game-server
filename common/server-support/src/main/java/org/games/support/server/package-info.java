package org.games.support.server;


//import org.games.impl.server.ProgramContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.context.ApplicationPidFileWriter;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Objects;
//import java.util.Scanner;
//
//@SpringBootApplication(proxyBeanMethods = false)
//public class App implements ProgramContext {
//    static final Logger log = LoggerFactory.getLogger(App.class);
//
//    public static SpringApplication app() {
//        return APP;
//    }
//
//    public static ConfigurableApplicationContext ctx() {
//        return CTX;
//    }
//
//    static SpringApplication APP;
//    static ConfigurableApplicationContext CTX;
//
//    public static void main(String[] args) {
//        SpringApplication app = APP = new SpringApplication(App.class);
//        app.addListeners(new ApplicationPidFileWriter());
//        ConfigurableApplicationContext ctx = CTX = app.run(args);
//        sync.start();
////        System.out.println(ctx.getBean(Gson.class));//yes
//        Scanner scanner = new Scanner(System.in);
//        String line;
//        outer:
//        while (true) {
//            log.info("wait command");
//            line = scanner.nextLine().trim();
//            switch (line) {
//                case "quit":
//                    break outer;
//                case "beans": {
//                    for (String name : ctx.getBeanDefinitionNames()) {
//                        log.info("{}", ctx.getBean(name).getClass());
//                    }
//                }
//                case "auto": {
//                    Arrays.stream(ctx.getBeanDefinitionNames())
//                            .map(ctx::getBean)
//                            .map(Object::getClass)
//                            .map(Class::getName)
//                            .filter(e -> e.contains("AutoConfiguration"))
//                            .distinct()
//                            .forEach(log::info)
//                    ;
//                }
//                default:
//                    log.info(line);
//            }
//        }
//        ctx.getBean(Server.class).shutdown();
//        ctx.close();
//        sync.sync();
//
//    }
//    static Sync sync = new Sync();
//    @Override
//    public void exec(Runnable r) {
//        if (Objects.isNull(r))
//            return;
//        if (sync.inWorkerThread(Thread.currentThread())) {
//            r.run();
//        } else {
//            sync.exec(r);
//        }
//    }
//
//    @Override
//    public void post(Runnable r) {
//        if (Objects.isNull(r))
//            return;
//        sync.exec(r);
//    }
//
//    @Override
//    public <T> T get(Class<T> clazz) {
//        return ctx().getBean(clazz);
//    }
//
//    @Override
//    public <T> Collection<T> gets(Class<T> clazz) {
//        return ctx().getBeansOfType(clazz).values();
//    }
//}