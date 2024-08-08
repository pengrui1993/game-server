package media.sounds;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;

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
    AudioFormat fmt = new AudioFormat(44100,16,2,true,false);
    Thread lt;
    Path file = Paths.get("/tmp/queue.tmp");
    OutputStream os;
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
        while(running.get()){
            try {
                buf.clear();
                SocketAddress receive = channel.receive(buf);
                buf.flip();
                aos.writeBytes(buf.array());
                if(aos.size()>=2048){
                    os.write(aos.buf(),0,aos.size());
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
    class Player extends PCMAudioPlayer {
        public Player() {
            super(SoundPCMServer.this.fmt, SoundPCMServer.this.file, SoundPCMServer.running);
            lt = this;
        }
    }
    protected static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    static AtomicBoolean running = new AtomicBoolean(true);
    public static void main(String[] args) throws Throwable {
        Thread server = new Thread(()->{ new SoundPCMServer(2222).run();});
        server.start();
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress sc = new InetSocketAddress("localhost", 2222);
        channel.connect(sc);
        while(!channel.isConnected())yield1();
        int len;
        ByteBuffer buf = ByteBuffer.allocate(512);
        RandomAccessFile file = new RandomAccessFile(PCMPlayer.path,"r");
        FileChannel fc = file.getChannel();
        while (-1!=(len = fc.read(buf))){
            int total = 0;
            buf.flip();/// Users/ pengrui/ learn/ emulator/ proj/ sdlpcm/ theshow. pcm
            while(true){
                total+=channel.send(buf,sc);
                if(len==total)break;
                else yield1();
            }
            buf.clear();
        }
        System.in.read();
        file.close();
        channel.close();
        running.set(false);
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
