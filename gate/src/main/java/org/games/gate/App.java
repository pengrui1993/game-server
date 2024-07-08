package org.games.gate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
@SpringBootApplication()//app:org.games.gate.App$$SpringCGLIB$$0@54f66455
//@SpringBootApplication(proxyBeanMethods = false)//app:org.games.gate.App@749f539e
public class App
{
    public static SpringApplication getApp(){
        return APP;
    }
    public static ConfigurableApplicationContext getContext(){
        return CTX;
    }
    static SpringApplication APP;
    static ConfigurableApplicationContext CTX;
    static PrintStream out(){
        return System.out;
    }
    public static void main( String[] args )
    {
        SpringApplication app = APP=new SpringApplication(App.class);
        app.addListeners(new ApplicationPidFileWriter());
        ConfigurableApplicationContext ctx = CTX=app.run(args);
//        System.out.println(ctx.getBean(Gson.class));//yes
        Scanner scanner = new Scanner(System.in);
        String line;
        outer:
        while(true){
            out().println("wait command");
            line=scanner.nextLine().trim();
            switch(line){
                case "quit":
                    break outer;
                case "beans":{
                    for (String name : ctx.getBeanDefinitionNames()) {
                        out().println(ctx.getBean(name).getClass());
                    }
                }
                case "auto":{
                    Arrays.stream(ctx.getBeanDefinitionNames())
                            .map(ctx::getBean)
                            .map(Object::getClass)
                            .map(Class::getName)
                            .filter(e->e.contains("AutoConfiguration"))
                            .distinct()
                            .forEach(clazz->out().println(clazz))
                    ;
                }
                default:
                    out().println(line);
            }
        }
        ctx.close();
    }

    /*
org.springframework.boot.autoconfigure.AutoConfigurationPackages$BasePackages
org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$ClassProxyingConfiguration
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$ClassProxyingConfiguration$$Lambda/0x00001800011f27e8
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration
org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration
org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration
org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration$StandardGsonBuilderCustomizer
org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration
org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration
org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration
org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration
org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration
     */
}
