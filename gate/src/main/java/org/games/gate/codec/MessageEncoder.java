package org.games.gate.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.games.constant.Const;
import org.games.gate.ProgramContext;
import org.games.message.Message;

import java.math.BigInteger;

public class MessageEncoder extends MessageToByteEncoder<Message> {
    private final ProgramContext cc;
    public MessageEncoder(ProgramContext cc) {
        this.cc=cc;
    }
    int CLIENT_MAGIC = Const.CLIENT_MAGIC;
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        byte[] msgBody = EncoderHandler.getEncoder(msg.type()).encode(msg);
        if(msgBody.length==0)return;
        out.writeInt(CLIENT_MAGIC);
        byte[] header = new byte[10];
        //TODO add header info to write
        out.writeBytes(header);
        out.writeBytes(msgBody);
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
