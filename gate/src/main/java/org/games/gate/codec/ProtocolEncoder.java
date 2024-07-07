package org.games.gate.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.games.gate.session.Session;
import org.games.message.Message;
import org.games.message.MessageEncoder;

import java.math.BigInteger;

public class ProtocolEncoder extends MessageToByteEncoder<Message> {
    private final CodecContext ctx;
    public ProtocolEncoder(CodecContext cc) {
        ctx=cc;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        final MessageEncoder e = this.ctx.getEncoder(msg.type());
        byte[] encode = e.encode(msg);
        if(encode.length==0)return;
        MessageHandlerInfo info = new MessageHandlerInfo();
        //TODO
    }

    protected void demo(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // Convert to a BigInteger first for easier implementation.
        BigInteger v;
        if (msg instanceof BigInteger) {
            v = (BigInteger) msg;
        } else {
            v = new BigInteger(String.valueOf(msg));
        }
        // Convert the number into a byte array.
        byte[] data = v.toByteArray();
        int dataLength = data.length;

        // Write a message.
        out.writeByte((byte) 'F'); // magic number
        out.writeInt(dataLength);  // data length
        out.writeBytes(data);      // data
    }
}
