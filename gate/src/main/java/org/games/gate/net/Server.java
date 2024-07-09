package org.games.gate.net;

import io.netty.channel.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.games.event.netty.EventDecodedTrigger;
import org.games.event.netty.NodeEventDecoder;
import org.games.event.netty.NodeEventEncoder;
import org.games.gate.codec.CodecContext;
import org.games.gate.codec.ProtocolDecoder;
import org.games.gate.codec.ProtocolEncoder;
import org.games.gate.evt.GateEventEmitter;
import org.games.gate.session.Session;
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
    public interface SessionAccessor{
        Session get(Object ctx);
    }
    static final Logger log = LoggerFactory.getLogger(Server.class);
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    @Resource
    private ServerHandler handler;
    @Resource
    private CodecContext cc;
    @Resource
    private EventDecodedTrigger trigger;
    @Resource
    private GateEventEmitter emitter;
    @Resource
    private SessionAccessor sessions;
    @Value("${config.server.port:2999}")
    private int port;
    private Channel serverSideChannel;
    @PostConstruct
    private void init(){
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
                        p.addLast(new NodeEventDecoder(trigger));
                        p.addLast(new ProtocolDecoder(cc));
                        p.addLast(new ProtocolEncoder(cc));
                        p.addLast(new NodeEventEncoder());
                        p.addLast(handler);
                    }
                });
        // Start the server.
        ChannelFuture bind = b.bind(port);
        serverSideChannel =bind.channel();
    }
    public void shutdown(){
        while(true){
            try {
                // Wait until the server socket is closed.
                serverSideChannel.close().sync();
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
