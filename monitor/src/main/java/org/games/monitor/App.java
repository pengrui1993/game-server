package org.games.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.PrintStream;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
@SpringBootApplication
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
                        out().println(name+" ");
                    }
                }
                default:
                    out().println(line);
            }
        }
        ctx.close();
    }
}
