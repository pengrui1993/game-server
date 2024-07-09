package org.games.event;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sync {
    static final Thread.UncaughtExceptionHandler h = (t,e)->{
        System.err.println(t+":");
        e.printStackTrace(System.err);
    };
    private volatile boolean started = false;
    private volatile boolean running = false;
    private volatile boolean stopped = false;
    public Sync(){
        creator = Thread.currentThread();
        worker = new Thread(this::loop);
        worker.setDaemon(true);
        worker.setUncaughtExceptionHandler(h);
    }
    public synchronized void start(){
        if(started)return;
        if(stopped)return;
        worker.start();
        running = true;
        started = true;
    }
    private final Thread creator;
    private final Thread worker;
    public boolean inWorkerThread(Thread t){
        return worker == t;
    }
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    Thread interrupter;
    public void sync()  {
        if(Thread.currentThread()!=creator)
            throw new IllegalStateException("must be creator call that function");
        if(stopped)throw new IllegalStateException("already sync");
        running = false;
        interrupter = creator;
        worker.interrupt();
        try {
            worker.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Runnable run;
        while(Objects.nonNull(run=queue.poll())){
            run.run();
        }
        stopped = true;
    }
    private void loop(){
        while(running){
            try {
                Runnable take = queue.take();
                take.run();
            } catch (InterruptedException e) {
                if(interrupter==creator){
                    interrupter = null;
                }else{
                    h.uncaughtException(worker,e);
                }
            }
        }
    }
    public void exec(Runnable runner){
        if(null==runner)return;
        if(!running){
            List<StackTraceElement> stack = Arrays.stream(new RuntimeException().getStackTrace()).toList();
            System.out.println("warning:stopped but add tasked,"+stack);
        };
//        final Thread cur = Thread.currentThread();
//        if(cur==worker){}
//        if(cur==creator){}
        try {
            queue.put(runner);
        } catch (InterruptedException e) {
            h.uncaughtException(creator,e);
        }
    }
}
