package media.ffmpeg.net;

import media.ffmpeg.EncodeAudio;
import media.sounds.PCMAudioPlayer;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * pcm to mp2 see:
 * {@link EncodeAudio#main(String[])}
 * set config choice = 1
 * {@link EncodeAudio#choice)}
 *
 */
public class MP2SoundServerRefactor {
    static final PrintStream out = System.out;
//    static final String mp2File = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
    static final String mp2File = "/tmp/java.out";//ffplay /tmp/java.out
    static final AtomicBoolean running = new AtomicBoolean(true);
    static Path file = Paths.get("/tmp/queue.tmp");
    static AudioFormat fmt = new AudioFormat(44100,16,2,true,false);
    Thread lt;
    long last;
    DatagramChannel channel;
    ServerDecoder dec;
    OutputStream os;
    final Queue<Runnable> queue = new ConcurrentLinkedDeque<>();
    final Thread queueWorker = new Thread(()->{
        try {
            Files.deleteIfExists(file);
            os = Files.newOutputStream(file
                    , StandardOpenOption.CREATE_NEW
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(running.get()){
            Runnable peek = queue.poll();
//            if(Objects.nonNull(dec)&&now()-last>1000) Utils.enqueue(queue,dec,null,0,os);//crash by that line
            if(null==peek){
                yield1();
                continue;
            }
            try{
                peek.run();
            }catch(Throwable t){
                t.printStackTrace(out);
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });

    public MP2SoundServerRefactor(int port){
        queueWorker.setDaemon(true);
        queueWorker.start();
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.socket().setSoTimeout(1000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        synchronized (MP2SoundServer.class){
            dec = new Decoder();
        }
    }
    static long now(){return System.currentTimeMillis();}
    public void run(){
        //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm
        ByteBuffer buf = ByteBuffer.allocate(512);
        new Player().start();
        while(running.get()){
            try {
                buf.clear();
                SocketAddress receive = channel.receive(buf);
                buf.flip();
                last = now();
                final byte[] b0;
                buf.get(b0= new byte[buf.remaining()]);
                Utils.enqueue(queue,dec,b0,b0.length,os);
            }catch (Throwable e) {
                if(e instanceof ClosedByInterruptException){
                    break;
                }
                System.out.println("timeout "+e);
            }
        }
        try {
            lt.join();
        } catch (InterruptedException e) {
            return;
        }
    }
    class Player extends PCMAudioPlayer {
        public Player() {
            super( fmt, file, running);
            log = false;
            lt = this;
        }
    }
    static void mp2_file(Consumer<byte[]> c){
        RandomAccessFile f;
        try {
            f = new RandomAccessFile(mp2File,"r");
            System.out.println("file.size:"+f.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int len;
        while(true){
            byte[]buf = new byte[512];
            try {
                len = f.read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(len==-1)break;
            if(len!=buf.length)buf = Arrays.copyOf(buf, len);
            c.accept(buf);
            sleep(1);

        }
        try {
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void sleep(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws Throwable {
        MP2SoundServerRefactor s=new MP2SoundServerRefactor(2222);
        Thread server = new Thread(s::run);
        server.start();
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress sc = new InetSocketAddress("localhost", 2222);
        channel.connect(sc);
        while(!channel.isConnected())yield1();
        mp2_file((buf)->{
            ByteBuffer wrap = ByteBuffer.wrap(buf);
            try {
                channel.send(wrap,sc);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        });
        System.in.read();
        Utils.process(s.dec,null,0,s.os);
        channel.close();
        running.set(false);
        server.interrupt();
        server.join();
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
