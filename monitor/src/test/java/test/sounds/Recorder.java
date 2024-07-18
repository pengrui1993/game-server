package test.sounds;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
public class Recorder {
    enum State{
        INIT,OPENED,STARTED,STOPPED,CLOSED
    }
    private State state;
    private final AudioFormat fmt;
    private TargetDataLine line;
    private final int bufSize;
    private byte[] buf;
    private int len;
    private final BiConsumer<byte[],Integer> callback;
    private boolean sync;
    private Executor executor;
    public Recorder(BiConsumer<byte[],Integer> cb,Executor e){
        fmt = new AudioFormat(44100.f,8,1,false,false);
        bufSize = 1024;
        sync = false;
        state = State.INIT;
        callback = Objects.requireNonNull(cb);
        executor = Optional.ofNullable(e).orElse(Runnable::run);
    }
    public Recorder setSync(boolean s){
        sync = s;
        return this;
    }
    public boolean open() throws LineUnavailableException {
        if(sync)synchronized (this){return _open();}
        return _open();
    }
    public boolean start(){
        if(sync)synchronized (this){return _start();}
        return _start();
    }
    public boolean stop(){
        if(sync)synchronized (this){return _stop();}
        return _stop();
    }
    public boolean close(){
        if(sync)synchronized (this){return _close();}
        return _close();
    }
    private boolean _close(){
        if(State.STOPPED!=state)return false;
        line.close();
        state = State.CLOSED;
        return true;
    }
    private boolean _start(){
        if(State.OPENED!=state)return false;
        line.start();
        state = State.STARTED;
        return true;
    }

    private boolean _stop(){
        if(State.STARTED!=state)return false;
        line.stop();
        state = State.STOPPED;
        return true;
    }
    private boolean _open() throws LineUnavailableException {
        if(State.INIT!=state)return false;
        line = AudioSystem.getTargetDataLine(fmt);
        buf = new byte[bufSize];
        line.open(fmt);
        state = State.OPENED;
        return true;
    }
    public void tick(){
        if(!sync){ _tick();return;}
        synchronized (this){_tick();}
    }
    private void _tick(){
        List.of(()->{if(len>0) callback.accept(buf,len);}
                ,(Runnable)()-> len = line.read(buf,0,bufSize)).forEach(Runnable::run);
    }
}
