package org.games.gate.session;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.games.message.Message;

public class NoRoleSession implements Session{
    ApiForSession manager;
    ChannelHandlerContext fd;
    public NoRoleSession(Object ctx, ApiForSession sessionManager) {
        this.fd = ChannelHandlerContext.class.cast(ctx);
        this.manager = sessionManager;
    }
    @Override
    public void writeAndFlush(Message msg, Runnable r) {
        fd.writeAndFlush(msg).addListener((ChannelFutureListener) cf -> r.run());
    }
}
