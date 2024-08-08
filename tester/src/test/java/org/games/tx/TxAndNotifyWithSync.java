package org.games.tx;

import java.util.function.Consumer;

public class TxAndNotifyWithSync {
    TxSync txSync;
    NotifySync nSync;
    TxMgr txMgr;
    NotifySystem notifier;
    RequestService service;
    LogSync logSync;
    void onAction(Request req){
        txSync.exec(()->{
            nSync.exec( ()->notifier.preTxNotify());
            txMgr.doTransaction((tx) -> {
                nSync.exec(()->notifier.startedTxNotify());
                try {
                    service.doService(req);
                } catch (Throwable t) {
                    nSync.exec( () -> notifier.preRollbackNotify());
                    tx.rollback();
                    nSync.exec( () -> notifier.postRollbackNotify());
                    logSync.exec(()->t.printStackTrace(System.err));
                    return;
                }
                nSync.exec( ()->notifier.preCommitNotify());
                tx.commit();
                nSync.exec( ()->notifier.postCommitNotify());
            });
            nSync.exec( ()->notifier.postTxNotify());
        });
    }
}
interface TxSync {
    void exec(Runnable run);
    TxSync t = run -> {
        System.out.println("TxSync.exec");
        run.run();
    };
}
interface LogSync {
    void exec(Runnable run);
    LogSync l = run -> {
        System.out.println("LogSync.exec");
        run.run();
    };
}
interface TxMgr {
    interface Tx {
        default void rollback(){
            System.out.println("Tx.rollback");
        }
        default void commit(){
            System.out.println("Tx.commit");
        }
    }
    void doTransaction(Consumer<Tx> run);
    TxMgr t = run -> {
        System.out.println("TxMgr.doTransaction");
        run.accept(new Tx() {});
    };
}
interface NotifySystem{
    NotifySystem n = new NotifySystem() {
    };
    default void preTxNotify(){
        System.out.println("NotifySystem.preTxNotify");
    }
    default void postTxNotify(){
        System.out.println("NotifySystem.postTxNotify");
    }
    default void startedTxNotify(){
        System.out.println("NotifySystem.startedTxNotify");
    }
    default void preRollbackNotify(){
        System.out.println("NotifySystem.preRollbackNotify");
    }
    default void postRollbackNotify(){
        System.out.println("NotifySystem.postRollbackNotify");
    }
    default void preCommitNotify(){
        System.out.println("NotifySystem.preCommitNotify");
    }
    default void postCommitNotify(){
        System.out.println("NotifySystem.postCommitNotify");
    }

}
interface Request{}
interface RequestService{
    void doService(Request req);
    RequestService r = req -> System.out.println("RequestService.doService");
}
interface NotifySync{
    void exec(Runnable run);
    NotifySync n = run -> {
        System.out.println("NotifySync.exec");
        run.run();
    };
}