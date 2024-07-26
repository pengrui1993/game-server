package test.sounds;

import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Objects;

public class Generator {
    static final int BUFFER_SIZE = 2048;//multi n if it is exploded
    static final double PI = 3.14159;
    static final int INT_SAMPLE_RATE = 44100;
    static final float SAMPLE_RATE = INT_SAMPLE_RATE;
    static long tick = 0;
    static int channel = 2;
    static int seconds = 5;
    static long lastTicks = seconds* INT_SAMPLE_RATE;
    static double freq = 440;
    static int gen(byte[] buffer){
        if(buffer.length%2==1)return -1;
        int len = 0;
        if(tick>=lastTicks)return -1;
        while(len < buffer.length){
            final double gen = Math.sin(2 * PI * freq*(tick++ / SAMPLE_RATE));//[-1,1]
            final double normal = 0.5*gen+0.5;// [0-1]
            final byte val = (byte)(0xff&((int)(255*normal)));
            for(int i=0;i<channel;i++)
                buffer[len++] = val;
            if(tick>=lastTicks)return len;
        }
        return len;
    }

    static int gen(byte[] buffer,long limit){
        if(buffer.length%2==1)return -1;
        int len = 0;
        if(tick>=limit)return -1;
        while(len < buffer.length){
            final double gen = Math.sin(2 * PI * freq*(tick++ / SAMPLE_RATE));//[-1,1]
            final double normal = 0.5*gen+0.5;// [0-1]
            final byte val = (byte)(0xff&((int)(255*normal)));
            for(int i=0;i<channel;i++)
                buffer[len++] = val;
            if(tick>=limit)return len;
        }
        return len;
    }

    public static void main1(String[] args) throws Exception{
        AudioFormat fmt = new AudioFormat(SAMPLE_RATE,8,channel,false,false);
        int len;
        SourceDataLine line;
        byte[] buffer = new byte[BUFFER_SIZE];
        line = AudioSystem.getSourceDataLine(fmt);
        line.open(fmt, buffer.length);
        line.start();
        long start = now();
        long size = 0;
        while (-1!=(len = gen(buffer))){
            size+=len;
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
        System.out.println("time:"+(now()-start)+",size(kb):"+(size/1024));
    }
    static long now(){
        return System.currentTimeMillis();
    }

    static void encode(String in,String out) throws EncoderException {
        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm out.mp3 -y
        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm theshow.m4a -y
        Encoder encoder = new Encoder();
        for (String a : encoder.getAudioEncoders()) {
            System.out.print(a+" ");
//            if(Objects.nonNull(a))throw new RuntimeException("cannot convert");
        }
        MultimediaObject mo = new MultimediaObject(new File(in));
        EncodingAttributes att = new EncodingAttributes();
        att.setInputFormat("pcm_u8");
        att.setOutputFormat("aac");
        AudioAttributes at = new AudioAttributes();
        at.setBitRate(128000);
        at.setChannels(2);
        at.setSamplingRate(44100);
        at.setCodec("aac");
//            audio.setSamplingRate(Math.min(MAX_SAMPLING_RATE_OF_AUDIO,samplingRateOfAudio));
        att.setAudioAttributes(at);
        encoder.encode(mo,new File(out),att);
    }

    public static void main(String[] args) throws Exception {
//        main2(args);
//        main1(args);
        encode(gen,"/tmp/sin.aac");
    }
    static String gen = "/tmp/sin.pcm";

    public static void main2(String[] args) throws IOException {
        //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm
        //ffplay -f s16le -ch_layout stereo -ar 44100 theshow.pcm



        //ffplay -f s8 -ch_layout mono -ar 44100 /tmp/sin.pcm
        Path path = Paths.get(gen);
        Files.deleteIfExists(path);
        OutputStream os = Files.newOutputStream(path, StandardOpenOption.CREATE_NEW);
        byte[] buf = new byte[1024];
        int times = INT_SAMPLE_RATE*2/buf.length;
        for(int i=0;i<10*times;i++){
            int len = gen(buf,INT_SAMPLE_RATE*10);
            os.write(buf,0,len);
        }
        os.flush();
        os.close();
    }
}
