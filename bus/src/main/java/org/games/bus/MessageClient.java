package org.games.bus;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageClient {
//    {
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//    }
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        connection.close();
    }
}
