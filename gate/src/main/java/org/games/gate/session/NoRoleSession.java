package org.games.gate.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.games.gate.ProgramContext;

public class NoRoleSession implements Session{
    ProgramContext pc;
    Channel fd;
    public NoRoleSession(Object ctx, ProgramContext pc) {
        this.fd = Channel.class.cast(ctx);
        this.pc = pc;
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
