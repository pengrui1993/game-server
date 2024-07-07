package org.games.gate.codec;

import org.games.cmd.Command;
import org.games.cmd.CommandHeader;

public interface DecoderHandler {
    Command decode(CommandHeader header, byte[] decoded);
}
