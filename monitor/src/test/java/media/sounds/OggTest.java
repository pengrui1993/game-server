package media.sounds;

import org.gagravarr.ogg.OggFile;
import org.gagravarr.vorbis.VorbisAudioData;
import org.gagravarr.vorbis.VorbisFile;

import javax.sound.sampled.AudioFileFormat;
import java.io.*;
import java.util.Objects;
/*
https://github.com/Gagravarr/VorbisJava/blob/master/core/src/test/java/org/gagravarr/ogg/TestBasicRead.java
 */
public class OggTest {
    public static void demo() throws IOException {
        OggFile in = new OggFile(ClassLoader.getSystemResourceAsStream("test.ogg"));
        VorbisFile vfIN = new VorbisFile(in);

        int infoSize = vfIN.getInfo().getData().length;
        int commentSize = vfIN.getComment().getData().length;
        int setupSize = vfIN.getSetup().getData().length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        VorbisFile vfOUT = new VorbisFile(
                baos,
                vfIN.getInfo(),
                vfIN.getComment(),
                vfIN.getSetup()
        );

        VorbisAudioData vad;
        while( (vad = vfIN.getNextAudioPacket()) != null ) {
            vfOUT.writeAudioData(vad);
            byte[] data = vad.getData();
            System.out.print(data.length+" ");
        }
        System.out.println();
        vfIN.close();
        vfOUT.close();

        assertEquals(infoSize, vfOUT.getInfo().getData().length);
        assertEquals(commentSize, vfOUT.getComment().getData().length);
        assertEquals(setupSize, vfOUT.getSetup().getData().length);

        System.out.println(vfIN.getInfo().getChannels());
        System.out.println(vfIN.getInfo().getSampleRate());
        System.out.println(vfIN.getInfo().getNumChannels());
    }

    public static void main(String[] args) throws IOException {
        play();
    }
    public static void play() throws IOException {
        OggFile in = new OggFile(ClassLoader.getSystemResourceAsStream("test.ogg"));
        VorbisFile vfIN = new VorbisFile(in);

        int infoSize = vfIN.getInfo().getData().length;
        int commentSize = vfIN.getComment().getData().length;
        int setupSize = vfIN.getSetup().getData().length;
//        System.out.println(vfIN.getComment().getAllComments());//{artist=[cullam Bruce-Lockhart], title=[Dawning Fanfare]}
        PlayerContext pc = new PlayerContext(vfIN.getInfo().getSampleRate(),8
                ,vfIN.getInfo().getChannels(),false,false);
        pc.start();
        VorbisAudioData vad;
        while( (vad = vfIN.getNextAudioPacket()) != null ) {
            byte[] data = vad.getData();
            System.out.print(data.length+" ");//17 39 18 19 21 20 23 39 38 31 32 33 37 37 185 162
//            pc.write(data);
        }
        pc.stop();;
        System.out.println();
        vfIN.close();
    }
    static void assertEquals(Object l,Object r){
        if(Objects.equals(l,r)){
            System.out.println("equals");
        }else{
            System.out.println("no");
        }
    }
    void fmt(){
        AudioFileFormat.Type aiff = AudioFileFormat.Type.AIFF;
    }
}
