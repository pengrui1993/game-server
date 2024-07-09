package org.games.event;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sync {
    static final Thread.UncaughtExceptionHandler h = (t,e)->{
        System.err.println(t+":");
        e.printStackTrace(System.err);
    };
    public Sync(){
        creator = Thread.currentThread();
        worker = new Thread(this::loop);
        worker.setDaemon(true);
        worker.setUncaughtExceptionHandler(h);
        worker.start();
    }
    private final Thread creator;
    private final Thread worker;
    public boolean inWorkerThread(Thread t){
        return worker == t;
    }
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    private void loop(){
        while(true){
            try {
                Runnable take = queue.take();
                take.run();
            } catch (InterruptedException e) {
                h.uncaughtException(worker,e);
            }
        }
    }
    public void exec(Runnable runner){
        if(null==runner)return;
        assert Thread.currentThread()!=worker;
        try {
            queue.put(runner);
        } catch (InterruptedException e) {
            h.uncaughtException(creator,e);
        }
    }
}
