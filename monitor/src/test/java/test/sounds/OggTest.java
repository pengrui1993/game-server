package test.sounds;

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
    public static void main(String[] args) throws IOException {
        String ogg = "/Users/pengrui/learn/emulator/proj/sdlpcm/out.ogg";
        OggFile in = new OggFile(new FileInputStream(new File(ogg)));
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
            System.out.println(data.length);
        }

        vfIN.close();
        vfOUT.close();

        assertEquals(infoSize, vfOUT.getInfo().getData().length);
        assertEquals(commentSize, vfOUT.getComment().getData().length);
        assertEquals(setupSize, vfOUT.getSetup().getData().length);
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
