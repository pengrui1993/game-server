package org.games.tx;

public class TxAndNotifyWithLock
{
    NotifySync notifySync;
    NotifySystem notifier;
    TxMgr txMgr;
    Locker locker;
    RequestService service;
    LogSync logSync;
    void onAction(Request req){
        locker.lock();
        try{
            notifySync.exec(()->notifier.preTxNotify());
            txMgr.doTransaction((t)->{
                notifySync.exec(()->notifier.startedTxNotify());
                try{
                    service.doService(req);
                }catch (Throwable tr){
                    notifySync.exec(()->notifier.preRollbackNotify());
                    t.rollback();
                    notifySync.exec(()->notifier.postRollbackNotify());
                    logSync.exec(()->tr.printStackTrace(System.err));
                    return;
                }
                notifySync.exec(()->notifier.preCommitNotify());
                t.commit();
                notifySync.exec(()->notifier.postCommitNotify());
            });
            notifySync.exec(()->notifier.postTxNotify());
        }finally{
            locker.unlock();
        }
    }
}


interface Locker{
    void lock();
    void unlock();
    Locker locker= new Locker(){
        @Override
        public void lock() {
            System.out.println("Locker.lock");
        }
        @Override
        public void unlock() {
            System.out.println("Locker.unlock");
        }
    };
}