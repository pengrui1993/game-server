package org.games.auth.mq;

import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.games.constant.MqNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Component
public class Rabbit {
    public final static String QUEUE_NAME = MqNames.AUTH.name;
    static final Logger log = LoggerFactory.getLogger(Rabbit.class);
    Connection connection;
    Channel channel;

    @PreDestroy
    private void destroy() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
    @PostConstruct
    private void init(){
        boolean autoAck = true;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            DeliverCallback success = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                if(!autoAck)channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println(" [x] Received '" + message + "'");
            };
            CancelCallback failure =  consumerTag -> {
                System.out.println("cancel:");
                System.out.println(consumerTag);
            };
            channel.basicConsume(QUEUE_NAME, autoAck
                    , success,failure);
        } catch (IOException | TimeoutException e) {
            log.error("conn rabbit mq error");
            log.error(e.getMessage(),e);
            System.exit(-1);
        }
    }
}
