package org.games.gate;

import org.games.support.server.AbstractProgram;
import org.games.support.server.Sync;
import org.games.support.server.ProgramContext;
import org.games.gate.evt.GateEventRegister;
import org.games.gate.net.Server;
import org.games.gate.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

//@SpringBootApplication()//app:org.games.gate.App$$SpringCGLIB$$0@54f66455
@SpringBootApplication(proxyBeanMethods = false)//app:org.games.gate.App@749f539e
public class App extends AbstractProgram
{
    static final Logger log = LoggerFactory.getLogger(App.class);
    public static SpringApplication app(){
        return APP;
    }
    public static ConfigurableApplicationContext ctx(){
        return CTX;
    }
    static SpringApplication APP;
    static ConfigurableApplicationContext CTX;
    static ConfigurableApplicationContext ctx;

    @Override
    protected boolean handleLine(String line) {
        ConfigurableApplicationContext ctx = App.ctx;
        log.info("wait command");
        switch(line){
            case "quit":
                return true;
            case "beans":{
                for (String name : ctx.getBeanDefinitionNames()) {
                    log.info("{}",ctx.getBean(name).getClass());
                }
            }
            case "auto":{
                Arrays.stream(ctx.getBeanDefinitionNames())
                        .map(ctx::getBean)
                        .map(Object::getClass)
                        .map(Class::getName)
                        .filter(e->e.contains("AutoConfiguration"))
                        .distinct()
                        .forEach(log::info)
                ;
            }
            case "netty":{
                line = readLine();
                ctx.getBean(SessionManager.class).broadcast(line);
            }break;
            default:
                log.info(line);
        }
        return false;
    }

    public static void main(String[] args ) {
        SpringApplication app = APP=new SpringApplication(App.class);
        app.addListeners(new ApplicationPidFileWriter());
        CTX=ctx=app.run(args);
        sync.start();
//        System.out.println(ctx.getBean(Gson.class));//yes
        GateEventRegister bean = ctx.getBean(GateEventRegister.class);
        log.info(bean.toString());
        App a = ctx.getBean(App.class);
        a.loop(args);
        sync.sync();
        ctx.getBean(Server.class).shutdown();
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

    @Override
    public <T> T get(Class<T> clazz) {
        return ctx().getBean(clazz);
    }
    @Override
    public <T> Collection<T> gets(Class<T> clazz) {
        return ctx().getBeansOfType(clazz).values();
    }
}
