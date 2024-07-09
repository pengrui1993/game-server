package org.games.gate.codec;

import org.games.cmd.CommandHeader;
import org.games.constant.Const;
import org.games.constant.MessageType;
import org.games.event.netty.EventDecodedTrigger;
import org.games.message.MessageEncoder;

public interface CodecContext {
    DecoderHandler getDecoder(CommandHeader header);
    MessageEncoder getEncoder(MessageType type);
    int CLIENT_MAGIC = Const.CLIENT_MAGIC;
    int NODE_MAGIC = Const.NODE_MAGIC;
}
