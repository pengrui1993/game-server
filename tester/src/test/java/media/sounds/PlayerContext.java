package media.sounds;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class PlayerContext{
    SourceDataLine line;
    final javax.sound.sampled.AudioFormat fmt;
    public PlayerContext(float sampleRate, int sampleBits, int channel, boolean signed, boolean bigEndian){
        //44100,16,2,true,false
        fmt = new javax.sound.sampled.AudioFormat(sampleRate,sampleBits,channel,signed,bigEndian);
    }
    public void start(){
        try {
            line = AudioSystem.getSourceDataLine(fmt);        // get a DataLine from the AudioSystem
            line.open();                                        // open and
            line.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

    }
    public void write(byte[] b){
        write(b,b.length);
    }
    public void write(byte[] b,int len){
        line.write(b, 0, len);
    }
    public void stop(){
        stop(true);
    }

    public void stop(boolean force){
        if(!force)line.drain();
        line.close();
    }
}
