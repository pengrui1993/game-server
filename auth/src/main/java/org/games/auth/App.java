package org.games.auth;

import org.games.auth.net.Net;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
@ComponentScan("org.games.auth.net")
public class App 
{
    public static void main( String[] args ) throws IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class);
        Net bean = ctx.getBean(Net.class);
        System.in.read();
        bean.shutdown();
    }
}
