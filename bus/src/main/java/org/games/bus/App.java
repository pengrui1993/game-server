package org.games.bus;

import org.games.bus._mq.RabbitBinder;
import org.games.bus._net.Server;
import org.games.support.server.AbstractProgram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

@SpringBootApplication
public class App extends AbstractProgram {
    static final Logger log = LoggerFactory.getLogger(App.class);
    static SpringApplication app;
    static ConfigurableApplicationContext ctx;
    public static void main( String[] args )
    {
        SpringApplication app = App.app = new SpringApplication(App.class);
        app.addListeners(new ApplicationPidFileWriter());
        ConfigurableApplicationContext ctx = App.ctx = app.run(args);
        App a = ctx.getBean(App.class);
        a.loop();
    }
    @Override
    protected boolean handleLine(String line) {
        ConfigurableApplicationContext ctx = App.ctx;
        switch (line) {
            case "quit":
                return true;
            case "beans": {
                for (String name : ctx.getBeanDefinitionNames()) {
                    log.info("{}", ctx.getBean(name).getClass());
                }
            }
            case "auto": {
                Arrays.stream(ctx.getBeanDefinitionNames())
                        .map(ctx::getBean)
                        .map(Object::getClass)
                        .map(Class::getName)
                        .filter(e -> e.contains("AutoConfiguration"))
                        .distinct()
                        .forEach(log::info)
                ;
            }
            default:
                ctx.getBean(RabbitBinder.class).randomPublish(line);
        }
        return false;
    }
    @Override
    protected void preDestroy() {
        ctx.getBean(Server.class).shutdown();
        ctx.close();
    }
    @Override
    public <T> T get(Class<T> clazz) {
        return ctx.getBean(clazz);
    }
    @Override
    public <T> Collection<T> gets(Class<T> clazz) {
        return ctx.getBeansOfType(clazz).values();
    }
}
