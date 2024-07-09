package org.games.gate.session;

import org.games.cmd.Command;
import org.games.gate.cmd.ContextFactory;
import org.games.gate.cmd.HandlerFinder;
import org.games.message.Message;

public interface Session {
    interface ApiForSession {
        ContextFactory getCommandContextFactory();
        HandlerFinder getCommandHandlerFinder();
        void register(Object fd, Session session);
    }
    enum SessionType{
        NO_ROLE, USER,COMP_AUTH,COMP_BUS,COMP_CONFIG,COMP_LOGICS,COMP_USERS
    }
    default SessionType type(){ return SessionType.NO_ROLE;}
    default void writeAndFlush(Message msg, Runnable r){
        writeAndFlush((Object)msg,r);
    }
    default void writeAndFlush(Object obj){
        writeAndFlush(obj,()->{
           // System.out.println("post write and flush");
        });
    }
    void writeAndFlush(Object data,Runnable r);
    default void onCommand(Command command){}
    Object getFd();
}
