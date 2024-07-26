package test.sounds;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PCMPlayer {
//    static final String path = "/Users/pengrui/learn/emulator/etc.proj/sdlpcm/theshow.pcm";
    public static final String path = "/Users/pengrui/learn/emulator/proj/sdlpcm/theshow.pcm";
    private static final int SAMPLE_RATE = 22050*2;
    private static final int BUFFER_SIZE = 4096;
    //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm

//    void run(){
//        this((signed == true ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED),
//                sampleRate,
//                sampleSizeInBits,
//                channels,
//                (channels == AudioSystem.NOT_SPECIFIED || sampleSizeInBits == AudioSystem.NOT_SPECIFIED)?
//                        AudioSystem.NOT_SPECIFIED:
//                        ((sampleSizeInBits + 7) / 8) * channels,
//                sampleRate,
//                bigEndian);
//                  sampleRate = 44100,frameSize = 44100 ,sampleSizeInBits = 8,channels = 1, then frame_rate = 1
//                  sampleRate = 44100,frameSize = 44100 ,ampleSizeInBits = 16,channel = 1, then frame_rate = 2
//                  sampleRate = 44100,frameSize = 44100 ,sampleSizeInBits = 16,channel = 2, then frame_rate = 4

//    }
//    void norun(){
//        this.encoding = encoding;
//        this.sampleRate = sampleRate;
//        this.sampleSizeInBits = sampleSizeInBits;
//        this.channels = channels;
//        this.frameSize = frameSize;
//        this.frameRate = frameRate;
//        this.bigEndian = bigEndian;
//        this.properties = null;
//    }
    private static final AudioFormat NO_WORK =
        new AudioFormat(AudioFormat.Encoding.PCM_SIGNED
                , SAMPLE_RATE, 8, 2, 2, SAMPLE_RATE, false);
    /*
       the byte of data on per sample
       frameSize:4     LH LL RH RL
       frameSize:2     L R/R L
    */
    static AudioFormat FORMAT2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,SAMPLE_RATE
            ,16,2,/* 2*((16+7)/8) */4,SAMPLE_RATE,false);


    void test() throws IOException {
        File file = new File("/tmp/a/b/c.txt");
        for(int i=0;i<3;i++)System.out.println(file.getParentFile().mkdirs());
        System.out.println(file.createNewFile());
    }
    public static void main(String[] args) throws Exception{
//        play(0);
//        playGen();
        cmpFile();

    }
    static final String file2 = "/tmp/gen_java.pcm";
    static final String file = "/tmp/gen.pcm";
    static void cmpFile() throws IOException {
        InputStream cf = Files.newInputStream(Paths.get(file));
        InputStream jf = Files.newInputStream(Paths.get(file2));
        ByteArrayOutputStream co = new ByteArrayOutputStream();
        ByteArrayOutputStream jo = new ByteArrayOutputStream();
        int data;
        while(-1!=(data=cf.read()))co.write(data);
        while(-1!=(data=jf.read()))jo.write(data);
        byte[] cb = co.toByteArray();
        byte[] jb = jo.toByteArray();
        if(cb.length!=jb.length){
            System.out.printf("len not equal,c:%d,j:%d\n",cb.length,jb.length);
        }else{
            int cs = 0,js = 0;
            for(int i=0;i<cb.length;i++){
                cs+=cb[i];
                js+=jb[i];
            }
            System.out.printf("cmp result, c sum:%d,j sum:%d\n",cs,js);
        }
        cf.close();
        jf.close();
    }
    static void create(){
        File file = new File(file2);

        if(!file.exists()){
            file.getParentFile().mkdirs();
            try {
                boolean newFile = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    static void playGen() throws LineUnavailableException, IOException {
        AudioFormat fmt = new AudioFormat(44100/2,8,2,false,false);
        int len;
        SourceDataLine line;
        InputStream is;
        byte[] buffer = new byte[BUFFER_SIZE];
        line = AudioSystem.getSourceDataLine(fmt);
        line.open(fmt, BUFFER_SIZE);
        line.start();
        is = Files.newInputStream(Paths.get(file));
        while (-1!=(len = is.read(buffer)))line.write(buffer, 0, len);
        line.drain();
        line.stop();
        is.close();
    }
    public static void play(int al) throws IOException,LineUnavailableException {
        //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm
        AudioFormat fmt = al>0?FORMAT2:new AudioFormat(SAMPLE_RATE,16,2,true,false);//same as FORMAT3
        int len;
        SourceDataLine line;
        InputStream is;
        byte[] buffer = new byte[BUFFER_SIZE];
        line = AudioSystem.getSourceDataLine(fmt);
        line.open(fmt, BUFFER_SIZE);
        line.start();
        is = Files.newInputStream(Paths.get(path));
        while (-1!=(len = is.read(buffer)))line.write(buffer, 0, len);
        line.drain();
        line.stop();
        is.close();
    }

    public static void example(String[] args) throws LineUnavailableException {
        DataLine dl = null;
        SourceDataLine sdl = null;
        TargetDataLine tdl = null;
        Clip clip = null;
        Mixer mixer = null;
        Port port = null;
        AudioFormat fmt = FORMAT2;
        TargetDataLine target = AudioSystem.getTargetDataLine(fmt);
        target.addLineListener(event -> {
            Line line = event.getLine();
            line = dl;
                line = sdl;
                line = tdl;
                line = clip;
            line = mixer;
            line = port;
            System.out.println(event.getLine());
            System.out.println(event.getFramePosition());
            LineEvent.Type close = LineEvent.Type.CLOSE;
            LineEvent.Type open = LineEvent.Type.OPEN;
            LineEvent.Type start = LineEvent.Type.START;
            LineEvent.Type stop = LineEvent.Type.STOP;
            System.out.println(event.getType());
        });
        target.open(fmt);
        target.close();
    }

    public static void info(String[] args) throws LineUnavailableException {
        for (AudioFileFormat.Type t : AudioSystem.getAudioFileTypes()) {
            System.out.print(t+" ");
        }
        System.out.println();
        System.out.println("---");
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            System.out.println(info+" ");
        }
        System.out.println("---");

    }

}
