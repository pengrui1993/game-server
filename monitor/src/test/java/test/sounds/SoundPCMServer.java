package test.sounds;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class SoundPCMServer {
    DatagramChannel channel;
    public SoundPCMServer(int port){
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.socket().setSoTimeout(1000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    static AudioFormat fmt = new AudioFormat(44100,16,2,true,false);
    Thread lt;
    Path file = Paths.get("/tmp/queue.tmp");
    OutputStream os;
    RandomAccessFile raf;
    public void run(){
        //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm
        ByteBuffer buf = ByteBuffer.allocate(512);
        try {
            Files.deleteIfExists(file);
             os = Files.newOutputStream(file
                    , StandardOpenOption.CREATE_NEW
//                    , StandardOpenOption.DELETE_ON_CLOSE
            );
        }catch (IOException e) {
            System.out.println(e);
            return;
        }
        new Player().start();
        class BAO extends ByteArrayOutputStream{
            public byte[] buf(){ return super.buf;}
        }
        BAO aos = new BAO();
        while(running){
            try {
                buf.clear();
                SocketAddress receive = channel.receive(buf);
                buf.flip();
                aos.writeBytes(buf.array());
                if(aos.size()>=2048){
                    os.write(aos.buf());
                    aos.reset();
                }
            }catch (Throwable e) {
                if(e instanceof ClosedByInterruptException){
                    break;
                }
                System.out.println("timeout "+e);
            }
        }
        try {
            os.write(aos.buf());
            aos.reset();
            os.flush();
            os.close();
            Files.deleteIfExists(file);
        } catch (IOException e) {
        }
        try {
            lt.join();
        } catch (InterruptedException e) {
            return;
        }

    }
    class Player extends Thread{
        Player(){
            lt = this;
            setDaemon(true);
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
            while(running){
                try {
                    if(cur%(100*buf.length)==0){
                        System.out.println(cur);
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
                        Thread.yield();
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
    static boolean running = true;
    public static void main(String[] args) throws Throwable {
        Thread server = new Thread(()->{ new SoundPCMServer(2222).run();});
        server.start();
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress sc = new InetSocketAddress("localhost", 2222);
        channel.connect(sc);
        while(!channel.isConnected())Thread.yield();
        int len;
        ByteBuffer buf = ByteBuffer.allocate(512);
        RandomAccessFile file = new RandomAccessFile(PCMPlayer.path,"r");
        FileChannel fc = file.getChannel();
        while (-1!=(len = fc.read(buf))){
            int total = 0;
            buf.flip();
            while(true){
                total+=channel.send(buf,sc);
                if(len==total)break;
                else Thread.yield();
            }
            buf.clear();
        }
        System.in.read();
        file.close();
        channel.close();
        running = false;
        server.interrupt();
        server.join();
    }

    static void play(){
        try {
            PCMPlayer.play(0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
