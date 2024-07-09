package org.games.gate.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.games.cmd.Command;
import org.games.cmd.CommandHeader;
import org.games.constant.CommandType;
import org.games.constant.Const;
import org.games.gate.ProgramContext;

import java.math.BigInteger;
import java.util.List;

public class CommandDecoder extends ByteToMessageDecoder {
    private final ProgramContext cCtx;
    public CommandDecoder(ProgramContext cc) {
        cCtx =cc;
    }
    static final int MAGIC = Const.CLIENT_MAGIC;
    // hi bits -> low bits : G A M E
    public static void main(String[] args) {
        for (char c : "GAME".toCharArray()) {
            String hexString = Integer.toHexString(c);
            System.out.print(hexString);
        }
        System.out.println();
        System.out.println(Integer.toHexString(MAGIC));
    }

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
        final CommandHeaderInfo info =
                new CommandHeaderInfo(magic,version,command,length,body);
        Command cmd = DecoderHandler.getCommandDecoder(info).encode(info, body);
        out.add(cmd);
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
