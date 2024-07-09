package org.games.bus._mq;

import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.games.constant.EventMqMapper;
import org.games.constant.EventType;
import org.games.constant.MqNames;
import org.games.event.AbstractEvent;
import org.games.event.Event;
import org.games.event.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
/*
about exchanges
https://www.rabbitmq.com/tutorials/tutorial-three-java#exchanges
 */
@Component
public class RabbitBinder {
    static final Logger log = LoggerFactory.getLogger(RabbitBinder.class);
    Connection connection;
    Channel channel;
    public void randomPublish(String msg){
        String message = msg.isBlank()? "info: Hello World!" :msg;
        AbstractEvent evt;
        final int i = ThreadLocalRandom.current().nextInt(classes.size());
        String canonicalName = classes.get(i).getCanonicalName();
        try {
            Class<?> aClass = Class.forName(canonicalName);
            evt = (AbstractEvent)aClass.getConstructor().newInstance();
        } catch (Throwable e) {
            log.error(e.getMessage(),e);
            return;
        }
        evt.msg = message;
        try {
            channel.basicPublish(canonicalName, "", null, evt.toJson().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

    void test() throws IOException {
        for (Class<? extends Event> aClass : classes) {
            for (MqNames value : MqNames.values()) {
                String EXCHANGE_NAME = aClass.getCanonicalName();
                String queueName = value.name;
                channel.queueDeclare(queueName,false,false,true,null);
                //The fanout exchange is very simple. As you can probably guess from the name, it just broadcasts all the messages it receives to all the queues it knows.
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                channel.queueBind(queueName, EXCHANGE_NAME, "");

            }
        }
    }
    public final static String QUEUE_NAME = MqNames.BUS.name;
    @PostConstruct
    private void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        StringBuilder builder = new StringBuilder();
        for (MqNames value : MqNames.values()) {
            String queueName = value.name;
            channel.queueDeclare(queueName,false,false,true,null);
        }
        Map<Integer,Class<? extends Event>>map = new HashMap<>();
        for (Class<? extends Event> aClass : classes) {
            String EXCHANGE_NAME = aClass.getCanonicalName();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            try {
                Event event = aClass.getConstructor().newInstance();
                map.put(event.type().id, aClass);
            } catch (Throwable e) {
                log.error(e.getMessage(),e);
            }
        }
        for (EventMqMapper value : EventMqMapper.values()) {
            MqNames mq = value.mq;
            EventType evt = value.evt;
            int eventTypeId = evt.id;
            String queueName = mq.name;
            String EXCHANGE_NAME = map.get(eventTypeId).getCanonicalName();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            builder.append("[exchanger:")
                    .append(EXCHANGE_NAME)
                    .append(",queue:")
                    .append(queueName)
                    .append("]\n");
        }
        log.info("rabbit mq etc:\nall queues:{}\nall exchangers:{}\n{}"
                , Arrays.stream(MqNames.values()).map(e->e.name).toList()
                , classes.stream().map(Class::getCanonicalName).toList()
                ,builder);
        DeliverCallback ok = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            log.info(" [x] Received '" + message + "'");
        };
        CancelCallback no = consumerTag -> {
            log.error("error:{}",consumerTag);
        };
        channel.basicConsume(QUEUE_NAME, true, ok, no);
    }
    @PreDestroy
    private void destroy() throws IOException, TimeoutException {
            channel.close();
            connection.close();
    }

    static final List<Class<? extends Event>> classes = EventUtils.classes;

    public static void main(String[] args) {
        System.out.println(classes);
    }
}
