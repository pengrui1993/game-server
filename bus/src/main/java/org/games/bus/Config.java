package org.games.bus;

import org.games.event.Sync;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class Config {
    @Bean
    public Sync sync(){
        return new Sync();
    }
}
