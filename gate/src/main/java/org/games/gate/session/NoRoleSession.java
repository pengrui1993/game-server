package org.games.gate.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.games.message.Message;

public class NoRoleSession implements Session{
    ApiForSession manager;
    Channel fd;
    public NoRoleSession(Object ctx, ApiForSession sessionManager) {
        this.fd = Channel.class.cast(ctx);
        this.manager = sessionManager;
        manager.register(fd,this);
    }
    @Override
    public void writeAndFlush(Object buf, Runnable r) {
        fd.writeAndFlush(buf).addListener((ChannelFutureListener) cf -> r.run());
    }

    @Override
    public Object getFd() {
        return fd;
    }
}
