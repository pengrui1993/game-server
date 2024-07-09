package org.games.event.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.games.constant.Const;
import org.games.event.Event;

import java.math.BigInteger;
import java.util.List;
public class NodeEventDecoder extends ByteToMessageDecoder {
    static final int MAGIC = Const.CLIENT_MAGIC;
    static final int NODE = Const.NODE_MAGIC;
    private final EventDecodedTrigger trigger;

    public NodeEventDecoder(EventDecodedTrigger trigger) {
        this.trigger = trigger;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final int headerSize = 20;
        if (in.readableBytes() < headerSize) return;
        in.markReaderIndex();
        NodeEventHeader header = new NodeEventHeader();
        header.magic = in.readInt();
        if(header.magic!=NODE){
            in.resetReaderIndex();
            return;
        }
        header.version = in.readInt();
        header.eventId = in.readInt();
        header.roleId = in.readInt();
        header.bodyLen = in.readInt();
        if(header.bodyLen<0){
            in.resetReaderIndex();
            return;
        }
        if(in.readableBytes()<header.bodyLen){
            in.resetReaderIndex();
            return;
        }
        in.resetReaderIndex();
        in.skipBytes(headerSize);
        byte[] buf = new byte[header.bodyLen];
        in.readBytes(buf);
        System.out.printf("magic:%s,version:%s,eventId:%s\n",header.magic,header.version,header.eventId);
        System.out.println(new String(buf));
        NodeEventDecoderHandler handler = new NodeConnectedEventDecoderHandler();
        Event evt = handler.decode(header, buf);
        trigger.triggerEvent(ctx.channel(),evt);
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


