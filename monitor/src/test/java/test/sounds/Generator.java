package test.sounds;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Generator {
    static final int BUFFER_SIZE = 2048;
    static final double PI = 3.14159;
    static final int INT_SAMPLE_RAGE = 44100/2;
    static final float SAMPLE_RATE = INT_SAMPLE_RAGE;
    static long tick = 0;
    static int channel = 2;
    static int seconds = 5;
    static long lastTicks = seconds*INT_SAMPLE_RAGE;
    static int gen(byte[] buffer){
        if(buffer.length%2==1)return -1;
        int len = 0;
        if(tick>lastTicks)return -1;
        while(len < buffer.length){
            final double gen = Math.sin(440 * 2 * PI * (tick++ / SAMPLE_RATE));//[-1,1]
            final double normal = 0.5*gen+0.5;// [0-1]
            final byte val = (byte)(0xff&((int)(255*normal)));
            for(int i=0;i<channel;i++)buffer[len++] = val;
        }
        return len;
    }

    public static void main(String[] args) throws Exception{
        AudioFormat fmt = new AudioFormat(SAMPLE_RATE,8,channel,false,false);
        int len;
        SourceDataLine line;
        byte[] buffer = new byte[BUFFER_SIZE];
        line = AudioSystem.getSourceDataLine(fmt);
        line.open(fmt, BUFFER_SIZE);
        line.start();
        long start = now();
        while (-1!=(len = gen(buffer))){
            int offset = 0;
            int remain = len;
            while(remain>0){
                len = line.write(buffer, offset, remain);
                remain-=len;
                offset+=len;
            }
        }
        line.drain();
        line.stop();
        System.out.println("time:"+(now()-start));
    }
    static long now(){
        return System.currentTimeMillis();
    }
}
