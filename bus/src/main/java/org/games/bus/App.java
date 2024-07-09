package org.games.bus;

import org.games.event.Sync;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
    public static void main( String[] args )
    {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class);
        Sync sync = ctx.getBean(Sync.class);
        sync.exec(()->{
            System.out.println("App.main");
        });
    }
    @Bean
    public Sync sync(){
        return new Sync();
    }
}
