package org.games.cmd;

public interface CommandDecoder {
    Command encode(byte[] bytes);
}
