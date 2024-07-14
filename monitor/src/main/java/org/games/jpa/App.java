package org.games.jpa;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.graphql.GraphQlAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.UUID;

/*
 http://localhost:8080/h2-console
 change the jdbc url
 jdbc:h2:mem:xxxx
 */
@SpringBootApplication(proxyBeanMethods = false
,exclude = {GraphQlAutoConfiguration.class}
)
//@PropertySource(value={"classpath:application-test.properties"})//active ok
//@PropertySource(value={"file:///home/pengrui/IdeaProjects/game_server/monitor/src/main/resources/application-test.properties"})//active ok
public class App {
    public static void main(String[] args) throws IOException {
        //java -jar myproject.jar --spring.profiles.active=prod
        java.util.List<String> list = new java.util.ArrayList<>(Arrays.stream(args).toList());
//        list.add("--spring.config.location=classpath:application-test.properties");//active okactive ok
        list.add("--spring.profiles.active=test");//active ok
        SpringApplication app = new SpringApplication(App.class);
        ConfigurableApplicationContext ctx = app.run(list.toArray(new String[0]));
    }
    @Resource
    private ConfigurableApplicationContext ctx;
    @Value("${spring.h2.console.enable:false}")
    private boolean enabled;
    @Resource
    private BookRepository bookRepository;

    void tables() throws SQLException {
        DataSource bean = ctx.getBean(DataSource.class);
        Connection conn = bean.getConnection();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery("show tables");
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        while(rs.next()){
            for(int i=0;i<columnCount;i++){
                System.out.print( rs.getString(i+1)+" ");
            }
            System.out.println();
        }
        rs.close();
        statement.close();
        conn.close();
    }
    @PostConstruct
    private void init() throws SQLException {
//        System.out.println(enabled);
        Book book = new Book();
        book.isbn = UUID.randomUUID().toString();
        System.out.println(bookRepository.save(book));
        for (Book book1 : bookRepository.findAll()) {
            System.out.println(book1);
        }
    }
}

@Configuration(proxyBeanMethods = false)
class Config{
    @Bean
    public TransactionTemplate tt(PlatformTransactionManager t){
        return new TransactionTemplate(t);
    }
}