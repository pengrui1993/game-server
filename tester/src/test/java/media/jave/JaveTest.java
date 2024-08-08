package media.jave;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;

/**
 * some error happen
 */
class JaveTest{
    public static void main(String[] args) throws EncoderException {
        encode("/Users/pengrui/learn/ffmpeg/theshow.pcm","/tmp/sin.aac");
    }
    static boolean info = true;
    static void print(Encoder encoder) throws EncoderException {
        if(!info)return;
        for (String a : encoder.getAudioEncoders()) {
            System.out.print(a+" ");
//            if(Objects.nonNull(a))throw new RuntimeException("cannot convert");
        }
    }
    static void encode(String in,String out) throws EncoderException {
        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm out.mp3 -y
        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm theshow.m4a -y
        Encoder encoder = new Encoder();
        print(encoder);
        MultimediaObject mo = new MultimediaObject(new File(in));
        EncodingAttributes att = new EncodingAttributes();
//        att.setInputFormat("pcm_u8");
        att.setInputFormat("pcm_s16le");
//        att.setOutputFormat("aac");
        AudioAttributes at = new AudioAttributes();
        at.setBitRate(128000);
        at.setChannels(2);
        at.setSamplingRate(44100);
        at.setCodec("aac");
//        at.setSamplingRate(Math.min(MAX_SAMPLING_RATE_OF_AUDIO,samplingRateOfAudio));
        att.setAudioAttributes(at);
        encoder.encode(mo,new File(out),att);
    }
}

