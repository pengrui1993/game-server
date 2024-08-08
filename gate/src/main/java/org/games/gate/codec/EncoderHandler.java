package org.games.gate.codec;

import org.games.constant.MessageType;
import org.games.gate.App;
import org.games.message.Message;
import org.games.message.MessageEncoder;

import java.util.Map;
import java.util.stream.Collectors;

public interface EncoderHandler {
    class Holder{
        static App app;
        static Map<MessageType,MessageEncoder> map;
    }
    static App require(){
        App app = Holder.app;
        if(null!=app)return app;
        Holder.app = app = App.ctx().getBean(App.class);
        return app;
    }
    static MessageEncoder get(MessageType t){
        var map = Holder.map;
        if(null!=map)return map.get(t);
        map = Holder.map= require().gets(MessageEncoder.class)
                .stream()
                .collect(Collectors.toMap(MessageEncoder::type, v->v));
        return map.get(t);
    }
    static MessageEncoder getEncoder(MessageType type) {
        MessageEncoder e;
        e = (e=get(type))!=null?e:new MessageEncoder() {
            @Override
            public MessageType type() {
                return MessageType.NULL;
            }
            @Override
            public byte[] encode(Message msg) {
                return new byte[0];
            }
        };
        return e;
    }
}
