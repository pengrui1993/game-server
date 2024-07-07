package org.games.gate.codec;

import jakarta.annotation.Resource;
import org.games.cmd.Command;
import org.games.cmd.CommandDecoder;
import org.games.cmd.CommandHeader;
import org.games.constant.MessageType;
import org.games.gate.session.SessionManager;
import org.games.message.Message;
import org.games.message.MessageEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CodecContextImpl implements CodecContext {
    @Resource
    private List<MessageEncoder> encoders;
    @Resource
    private List<CommandDecoder> decoders;
    @Resource
    private SessionManager sessionManager;
    @Override
    public DecoderHandler getDecoder(CommandHeader header) {
        for (CommandDecoder decoder : decoders) {
            if(decoder.type().code==header.cmd()){
                return decoder::encode;
            }
        }
        return (h, b) -> Command.NULL;
    }
    @Override
    public MessageEncoder getEncoder(MessageType type) {
        for (MessageEncoder encoder : encoders) {
            if(encoder.type()==type)
                return encoder;
        }
        return new MessageEncoder() {
            @Override
            public MessageType type() {
                return MessageType.NULL;
            }
            @Override
            public byte[] encode(Message msg) {
                return new byte[0];
            }
        };
    }
}
