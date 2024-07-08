package org.games.bus;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import jakarta.annotation.PostConstruct;
import org.games.constant.EventType;
import org.games.constant.ServiceComponentType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RouterRule {
    class Target {
        Set<ServiceComponentType> receivers;
    }
    class Context{
        EventType type;
        ServiceComponentType source;
        Target target;
    }
    /*
        message: { messageId:1,event:1,source:1,data:{ user:""}}
        eventIds:[1,2,3,4,5,6,7,8]
        queueIds:[1,2,3]
        queueEvents:[//relations
             { queue:1,events:[1,2,6,7]}
            ,{ queue:2,events:[1,3,4,8]}
            ,{ queue:3,events:[1,3,5,8]}
        ]
     */
    @PostConstruct
    private void init(){

    }
    List<Context> contextList = new ArrayList<>();
    public interface Handler{
        void send(ServiceComponentType src,ServiceComponentType desc,Object event);
    }
    void dispatcher(ServiceComponentType src, EventType type, Object event,Handler handler){
        for (Context context : contextList) {
            if(context.source==src&&type==context.type){
                for (ServiceComponentType receiver : context.target.receivers) {
                    handler.send(src,receiver,event);//send to mq.target.queue
                }
            }
        }
    }


}
