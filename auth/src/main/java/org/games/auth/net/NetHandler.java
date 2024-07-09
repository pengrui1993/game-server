package org.games.auth.net;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.games.constant.Const;
import org.games.constant.SystemRoleType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import static org.games.auth.net.Net.SIZE;

@Component
public class NetHandler extends ChannelInboundHandlerAdapter {
    private final ByteBuf firstMessage;

    /**
     * Creates a client-side handler.
     */
    public NetHandler() {
        firstMessage = Unpooled.buffer(SIZE);
        for (int i = 0; i < firstMessage.capacity(); i ++) {
            firstMessage.writeByte((byte) i);
        }
    }
    Gson gson = new Gson();
    /*
        public int magic;
        public int version;
        public int eventId;
        public int roleId;
        public int bodyLen;
    */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println(msg);
//        ctx.writeAndFlush(firstMessage);
//        ByteBufAllocator alloc = ctx.alloc();

        if(msg instanceof ByteBuf bb){
            int i = bb.readableBytes();
            byte[] buf = new byte[i];
            bb.readBytes(buf);
            String req = new String(buf,StandardCharsets.UTF_8);
            System.out.println(req);
            if(!"hello netty".equals(req))return;
            String message = "ack netty";
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer(bytes.length + 20);
            int magic = Const.NODE_MAGIC;
            buffer.writeInt(magic);
            buffer.writeInt(1);//version
            buffer.writeInt(3);//eventId
            buffer.writeInt(SystemRoleType.AUTH.id);//eventId
            buffer.writeInt(bytes.length);
            buffer.writeBytes(bytes);
            ctx.writeAndFlush(buffer);
        }else{
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace(System.err);
        ctx.close();
    }
}
