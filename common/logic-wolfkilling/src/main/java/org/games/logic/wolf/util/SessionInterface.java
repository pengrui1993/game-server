package org.games.logic.wolf.util;

import org.games.logic.wolf.core.Major;
import org.games.logic.wolf.role.Role;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public interface SessionInterface {
    SessionInterface dummy = new SessionInterface() {};
    PrintStream out = System.out;
    default PrintStream out(){return System.out;}
    default void stub(){
        out();
    }
    default void notifyJoin(Object... params){stub();
        String who = params[0].toString();
        out.println("notify,"+who+" joined room");
    }
    default void notifyLeft(Object... params){stub();
        String who = params[0].toString();
        out.println("notify,left the room:"+who);
    }
    default void notifyStart(Object... params){stub();
        Map<String, Role> players = Map.class.cast(params[0]);
        out.println("notify,start the room:"+players);
    }
    default void notifyStateChange(Object... params){stub();
        Map<String,Role> users = Map.class.cast(params[0]);
        Major from = Major.class.cast(params[1]);
        Major to = Major.class.cast(params[2]);
        List<String> allow = users.entrySet()
                .stream()
                .filter(e -> NotifyUtil.notify(from, to, e.getValue()))
                .map(Map.Entry::getKey)
                .toList();
        out.printf("notify to %s,state change from->to [%s->%s]\n",allow,from,to);
    }
    default void notifyWolfAction(Object... params){stub();
        List<String> users = List.class.cast(params[0]);
        out.println("notify, client,wolf action:"+users);
    }
    default void notifyWolfSelect(Object... params){
        stub();
        List<String> wolfs = List.class.cast(params[0]);
        String wolf = String.valueOf(params[1]);
        String target = String.valueOf(params[2]);
        String pre = String.valueOf(params[3]);
        out.printf("notify,wolf:%s select %s, pre selected:%s,wolfs:%s\n",wolf,target,pre,wolfs);
    }
    default void notifyWitchSave(Object... params){stub();
        String witch = String.valueOf(params[0]);
        out.println("notify,witch save,id:"+witch);
    }
    default void notifyWitchKill(Object... params){stub();
        String witch = String.valueOf(params[0]);
        String target = String.valueOf(params[1]);
        out.println("notify,witch kill,id:"+witch+",target:"+target);
    }
    default void notifyWitchCancel(Object... params){stub();
        String witch = String.valueOf(params[0]);
        out.println("notify,witch cancel,id:"+witch);
    }
    default void notifyPredictorPredictor(Object... params){stub();
        Map.Entry<String,Boolean> verify = Map.Entry.class.cast(params[0]);
        out.println("notify,predictor "+verify.getKey()+" verify result:"+verify.getValue());
    }
    default void demo(Object... params){stub();}
}
