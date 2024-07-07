package org.games.gate.codec;

import org.games.cmd.CommandHeader;
import org.games.constant.MessageType;
import org.games.message.MessageEncoder;

public interface CodecContext {
    DecoderHandler getDecoder(CommandHeader header);
    MessageEncoder getEncoder(MessageType type);
    int MAGIC = ('G'<<(32-(1*8)))|('A'<<(32-(2*8)))|('M'<<(32-(3*8)))|('E'<<(32-(4*8)));

}
