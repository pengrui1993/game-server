package org.games.support.server;

import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;

public abstract class AbstractProgram implements ProgramContext{
    protected static Sync sync = new Sync();
    @Override
    public void exec(Runnable r) {
        if (Objects.isNull(r))
            return;
        if (sync.inWorkerThread(Thread.currentThread())) {
            r.run();
        } else {
            sync.exec(r);
        }
    }
    public void loop(){
        sync.start();
        Scanner scanner = new Scanner(System.in);
        String line;
        do {
            line = scanner.nextLine().trim();
        } while (!handleLine(line));
        preDestroy();
        sync.sync();
    }
    protected boolean handleLine(String line){
        switch (line) {
            case "quit":return true;
            default:
                System.out.println(line);
        }
        return false;
    }
    protected void preDestroy(){
//        ctx.getBean(Server.class).shutdown();
//        ctx.close();
    }
    @Override
    public void post(Runnable r) {
        if (Objects.isNull(r))
            return;
        sync.exec(r);
    }
    @Override
    public abstract <T> T get(Class<T> clazz);

    @Override
    public abstract <T> Collection<T> gets(Class<T> clazz);
}