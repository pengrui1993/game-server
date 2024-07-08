package org.games;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class RabbitSimple {
    private final static String QUEUE_NAME = "hello";

    static class Receiver{
        static Connection connection;
        static void close() throws IOException {
            if(Objects.nonNull(connection)){
                connection.close();
                connection = null;
            }
        }
        static void info(){
            System.out.println(connection);
        }
        public static void main(String[] argv) throws Exception {
            boolean autoAck = true;
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            channel.basicConsume(QUEUE_NAME, autoAck
                    , (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        if(!autoAck)channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        System.out.println(" [x] Received '" + message + "'");
                    }, consumerTag -> {
                        System.out.println("cancel:");
                        System.out.println(consumerTag);
            });
        }
    }
    static class Sender{
        public static void main(String[] args) throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel())
            {
                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                String message = "Hello World!";
                if(args.length>0)message= String.join(",",args);
                channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
                //System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final Thread receiver = new Thread(()->{
            try {
                Receiver.main(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        receiver.start();
        Scanner scanner = new Scanner(System.in);
        String line;
        outer:
        while(true){
            line = scanner.nextLine().trim();
            switch (line){
                case "quit":break outer;
                case "info":{
                    Receiver.info();
                }break;
                default:
                    String[] cmd = {line};
                    Sender.main(cmd);
            }
        }
        Receiver.close();
        receiver.join();
    }
}
