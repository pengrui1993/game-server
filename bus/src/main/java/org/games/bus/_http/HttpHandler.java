package org.games.bus._http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.games.constant.Const;
import org.games.support.server.ProgramContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HttpHandler extends SimpleChannelInboundHandler<HttpObject> {
    private static final byte[] CONTENT = { 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd' };
    private final ProgramContext pc;
    public HttpHandler(ProgramContext pc) {
        this.pc = pc;
    }
    static final Logger log = LoggerFactory.getLogger(HttpHandler.class);
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    boolean local(HttpHeaders headers){
        final String host = headers.get("Host");
        for (String s : List.of("127.0.0.1", "localhost")) if(host.startsWith(s))return true;
        return false;
    }
    protected DefaultFullHttpResponse process(ChannelHandlerContext ctx, HttpRequest req){
        DefaultFullHttpResponse response = null;
        final String uri = req.uri();
        final HttpMethod method = req.method();
        final HttpHeaders headers = req.headers();
        switch(uri){
            case Const.BUS_HTTP_HELLO->{
                if(!local(headers))break;
                ByteBuf buf = Unpooled.wrappedBuffer(CONTENT);
                response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
                        buf);
            }
            case Const.BUS_HTTP_PREPARED ->{
                if (local(headers)) {
                    ByteBuf buf = ctx.alloc().directBuffer(1);
                    buf.writeByte(pc.isPrepared()?'1':'0');
                    response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
                            buf);
                }
            }
            default -> {
                log.warn("{} no handler",uri);
                System.out.println(method+" "+uri);
                for (Map.Entry<String, String> entry : req.headers().entries()) {
                    System.out.println(entry);
                }
            }
        }
        if(Objects.isNull(response)){
            response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.NOT_FOUND,
                    Unpooled.wrappedBuffer(Unpooled.EMPTY_BUFFER));
        }
        response.headers()
                .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
//                        .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof HttpRequest req) {
            boolean keepAlive = HttpUtil.isKeepAlive(req);
            FullHttpResponse response = process(ctx,req);
            boolean dftNoKeepAlive = !req.protocolVersion().isKeepAliveDefault();
            // Tell the client we're going to close the connection if request's states of connection is close
            AsciiString val = keepAlive&&dftNoKeepAlive?HttpHeaderValues.KEEP_ALIVE:HttpHeaderValues.CLOSE;
            response.headers().set(HttpHeaderNames.CONNECTION,val);
            //close if no keep alive when written;
            ChannelFutureListener post = keepAlive?(cf)->{}:ChannelFutureListener.CLOSE;
            ctx.write(response).addListener(post);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}