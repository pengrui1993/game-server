package org.games.auth.net;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.util.Objects;
import java.util.Optional;

//@Component
public class Net {
//    static final String HOST = System.getProperty("host", "127.0.0.1");
//    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    @Value("${config.server.port}")
    private int gatePort;
    @Value("${config.server.host}")
    private String gateHost;
    @Value("${config.server.hostname}")
    private String gateHostname;
    @Resource
    private NetHandler netHandler;
    EventLoopGroup group;
    private boolean conn(){
        try {// Start the client.
            group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(netHandler);
                        }
                    });
            closeFuture = b.connect(gateHost, gatePort).sync();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
            this.shutdown();
            System.exit(-1);
        } catch (Throwable t){
            t.printStackTrace(System.err);
            return false;
        }
        return true;
    }
    @PostConstruct
    private void init(){
        while (true) {
            if(Objects.nonNull(group))
                group.shutdownGracefully();
            if(conn())break;
            sleep(5000);
        }
        toGate = closeFuture.channel();
        // Wait until the connection is closed.
    }
    static void sleep(long ms){
        if(ms<=0)return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }

    }
    public void shutdown(){
        try {
            Optional.ofNullable(toGate).ifPresent(e-> {
                while(true){
                    try {
                        e.close().sync();
                    } catch (InterruptedException ex) {
                        continue;
                    }
                    break;
                }
            });
        }finally {
            group.shutdownGracefully();
        }
    }
    ChannelFuture closeFuture;
    Channel toGate;
}
