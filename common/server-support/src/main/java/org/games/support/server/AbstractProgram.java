package org.games.support.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

public abstract class AbstractProgram implements ProgramContext{
    protected static Sync sync = new Sync();
    protected boolean prepared =false;

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
    @Override
    public boolean isPrepared(){
        return prepared;
    }
    protected BufferedReader scanner;
    boolean stdin;
    protected String readLine(){
        try {
            return scanner.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loop(String...filepath){
        sync.start();
        prepared = true;
        scanner = new BufferedReader(new InputStreamReader(System.in));
        stdin = true;
        String line;
        while(true){
            try{
                line = readLine();
                if(Objects.isNull(line)){
                    System.out.printf("stdin preparing %s,%s\n"
                            ,System.currentTimeMillis(),filepath[0]);
                    Thread.sleep(1000);
                    if(filepath.length>0){
                        if(stdin)stdin = false; else scanner.close();
                        scanner = new BufferedReader(new FileReader(filepath[0]));
                    }
                    continue;
                }
                System.out.println("line:"+line);
                if(handleLine(line))break;
            }catch (Throwable e){
                if(e instanceof NoSuchElementException){
                    System.err.println("NoSuchElementException: No line found");
                }
                e.printStackTrace(System.err);
//            Runtime.getRuntime().addShutdownHook(new Thread(()-> System.err.println("program done")));
            }
        }
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