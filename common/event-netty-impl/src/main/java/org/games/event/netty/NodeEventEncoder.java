package org.games.event.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class NodeEventEncoder
//        extends MessageToByteEncoder<Message>
    extends ChannelOutboundHandlerAdapter
{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(msg instanceof byte[] b){
            ctx.writeAndFlush(Unpooled.wrappedBuffer(b),promise);
        }else{
            super.write(ctx, msg, promise);
        }
    }
}
