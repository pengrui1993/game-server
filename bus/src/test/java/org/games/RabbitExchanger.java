package org.games;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class RabbitExchanger {
    private static final String EXCHANGE_NAME = "logs";
    static class EmitLog {
        public static void main(String[] argv) throws Exception {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                String message = argv.length < 1 ? "info: Hello World!" :
                        String.join(" ", argv);
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println(" [x] Sent '" + message + "'");
            }
        }
    }
    static class ReceiveLogs  extends Thread{
        Connection connection;
        Channel channel;
        @Override
        public void run() {
            try {
                doStart();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        public void doStart() throws IOException, TimeoutException {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        }
        public void doClose() throws IOException, TimeoutException {
            channel.close();
            connection.close();
        }
        public void info(){
            System.out.println(connection+":"+channel);
        }
    }

    public static void main(String[] args) throws Exception {
        List<ReceiveLogs> listeners = new ArrayList<>();
        for(int i=0;i<3;i++){
            listeners.add(new ReceiveLogs());
        }
        for (Thread listener : listeners) {
            listener.start();
        }
        Scanner scanner = new Scanner(System.in);
        String line;
        outer:
        while(true){
            line = scanner.nextLine().trim();
            switch (line){
                case "quit":
                    break outer;
                case "info":
                    listeners.forEach(ReceiveLogs::info);
                    break;
                default:
                    String[] msg = {line};
                    EmitLog.main(msg);
            }
        }
        for (ReceiveLogs listener : listeners) {
            listener.doClose();
        }
    }
}
