package org.games;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Handler extends IoHandlerAdapter {
    static final Logger log = LoggerFactory.getLogger(Handler.class);
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (session == null || message == null) {
            return;
        }
        try {
            IoBuffer buf = (IoBuffer) message;
            Object msg = decode(buf);
            if (msg != null) {
                //将消息交给后面的事件循环线程处理
//                msgLoop.msgReceived(msg, session);
            } else {
                log.error("decode msg error.");
                //session.close(true);
            }
        } catch (Exception e) {
            log.error("receive message error: ", e);
            //session.close(true);
        }
    }
    Object decode(IoBuffer buf){
//            NetMsgBase msg = MsgDecoder.decode(buf);
        return buf;
    }
}
