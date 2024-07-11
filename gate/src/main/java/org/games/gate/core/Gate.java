package org.games.gate.core;

import org.games.gate.core.iface.GateCaredServiceNodeEvent;
import org.games.gate.core.iface.GateForServiceEvent;

public interface Gate extends GateCaredServiceNodeEvent, GateForServiceEvent {

    boolean allServerNodePrepared();
    boolean busPrepared();
    default boolean prepared(){
        return allServerNodePrepared();
    }

    void onAllServerNodePrepared();
    void onSomeNodeCrash();
}
