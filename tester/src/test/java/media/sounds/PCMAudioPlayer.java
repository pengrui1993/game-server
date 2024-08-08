package media.sounds;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;


public class PCMAudioPlayer extends Thread{
    RandomAccessFile raf;
    AudioFormat fmt;
    Path file;
    AtomicBoolean running;
    protected boolean log = true;
    public PCMAudioPlayer(AudioFormat fmt,Path file,AtomicBoolean running){
        this.fmt = fmt;
        this.file = file;
        this.running = running;
        setDaemon(true);
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        SourceDataLine line;
        try {
            line = AudioSystem.getSourceDataLine(fmt);
            line.open(fmt, 4096);
            // System.out.println(line.getBufferSize());//4096
            raf = new RandomAccessFile(file.toFile(),"r");
        } catch (Throwable e) {
            return;
        }
        line.start();
        int cur = 0;
        byte[] buf = new byte[1024*4];
        int len;
        boolean skip = ThreadLocalRandom.current().nextBoolean();
        while(running.get()){
            try {
                if(cur%(100*buf.length)==0&&log){
                    System.out.println("player running,cur:"+cur);
                }
                if(skip){
                    raf.seek(0);
                    int ns = raf.skipBytes(cur);
                    if(ns!=cur){
                        System.out.println("skip error");
                    }
                }else{
                    raf.seek(cur);
                }

                len = raf.read(buf);
                if(-1==len){
                    yield1();
                    continue;
                }
                cur +=len;
                int wl = len;
                int off = 0;
                do {
                    int write = line.write(buf, off, wl);
                    if (write != len) {
                        System.out.print("invalid write\n");
                    }
                    wl -= write;
                    off += write;
                } while (wl != 0);
            } catch (IOException e) {
                if(e instanceof NoSuchFileException){
                    yield1();
                    continue;
                }
                System.out.println(e);
                break;
            }
        }
        line.drain();
        line.stop();
        try {
            raf.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}