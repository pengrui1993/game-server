package org.games.bus._http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.games.support.server.ProgramContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class HttpServer {
    @Value("${config.server.http.port}")
    private int port;
    static final Logger log = LoggerFactory.getLogger(HttpServer.class);
    @Resource
    private ProgramContext pc;
    boolean stopByStdIn = false;
    // Configure the server.
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    Channel ch;
    @PostConstruct
    private void init() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelCreator(pc));
        ch = b.bind(port).sync().channel();
        log.info("Open your web browser and navigate to {}{}","http://127.0.0.1:",port);
        if(stopByStdIn){
            new Thread(()->{
                try {
                    System.in.read();
                } catch (IOException ignore) {
                }
                ch.close();
            }).start();
        }
    }
    @PreDestroy
    private void destroy()throws Exception {
        ch.close().addListener((p)-> ch.closeFuture().addListener(e->{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

    }
}
