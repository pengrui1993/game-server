package org.games.auth.net;


import io.netty.channel.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Optional;

@Component
public class Net {
//    static final String HOST = System.getProperty("host", "127.0.0.1");
//    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));
    @Value("${config.gate.port:1999}")
    private int gatePort;
    @Value("${config.gate.host:localhost}")
    private String gateHost;
    @Value("${config.gate.hostname}")
    private String gateHostname;
    @Resource
    private NetHandler handler;
    EventLoopGroup group = new NioEventLoopGroup();
    @PostConstruct
    private void init(){
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        //p.addLast(new LoggingHandler(LogLevel.INFO));
                        p.addLast(handler);
                    }
                });
        try {// Start the client.
            closeFuture = b.connect(gateHost, gatePort).sync();
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        } catch (Throwable t){
            t.printStackTrace(System.err);
            shutdown();
            System.exit(-1);
        }
        toGate = closeFuture.channel();
        // Wait until the connection is closed.
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
