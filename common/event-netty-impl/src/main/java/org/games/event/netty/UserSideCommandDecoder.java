package org.games.event.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.games.constant.Const;

import java.math.BigInteger;
import java.util.List;

public class UserSideCommandDecoder extends ByteToMessageDecoder {
    static final int MAGIC = Const.CLIENT_MAGIC;
    static final int NODE = Const.NODE_MAGIC;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int headerSize = 128;//TODO
        if (in.readableBytes() < headerSize)return;
        in.markReaderIndex();
        int magic = in.readInt();
        if(MAGIC!=magic){
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magic);
        }
        int version = in.readInt();
        int command = in.readInt();
        long bodyLength = in.readUnsignedInt();
        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }
        int frameMaxSize = (Integer.MAX_VALUE>>1)-headerSize;
        if(bodyLength>frameMaxSize){
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magic);
        }
        //TODO reserved
        //reserved done
        in.resetReaderIndex();
        in.skipBytes(headerSize);
        int length = (int)bodyLength;
        byte[] body = new byte[length];
        in.readBytes(body);
//        final CommandHeaderInfo info =
//                new CommandHeaderInfo(magic,version,command,length,body);
//        final DecoderHandler decoder = cCtx.getDecoder(info);
//        final Command cmd = decoder.decode(info, body);
//        out.add(cmd);
    }

    private void demo(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
        // Wait until the length prefix is available.
        if (in.readableBytes() < 5) {
            return;
        }
        in.markReaderIndex();
        // Check the magic number.
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }
        // Wait until the whole data is available.
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        // Convert the received data into a new BigInteger.
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);

        out.add(new BigInteger(decoded));
    }
}
