package org.games.gate.net;

import io.netty.channel.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.games.gate.codec.CodecContext;
import org.games.gate.codec.ProtocolDecoder;
import org.games.gate.codec.ProtocolEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
@Component
public class Server {
    static final Logger log = LoggerFactory.getLogger(Server.class);
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    @Resource
    private ServerHandler handler;
    @Resource
    private CodecContext cc;
    @Value("${config.server.port:2999}")
    private int port;
    private Channel serverSideChannel;
    private ChannelFuture serveSideCloseFuture;
    @PostConstruct
    private void init(){
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            //p.addLast(new LoggingHandler(LogLevel.INFO));
                            p.addLast(new ProtocolDecoder(cc));
                            p.addLast(new ProtocolEncoder(cc));
                            p.addLast(handler);
                        }
                    });
            // Start the server.
            ChannelFuture bind = b.bind(port);
            serverSideChannel =bind.channel();
            serveSideCloseFuture =bind.sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(),e);
            System.exit(-1);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    public void destroy(){
        while(true){
            try {
                // Wait until the server socket is closed.
                serveSideCloseFuture.sync();
            } catch (InterruptedException e) {
                continue;
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
            break;
        }
    }
}
