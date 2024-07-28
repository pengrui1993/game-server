//package test.jave;
//
//import ws.schild.jave.Encoder;
//import ws.schild.jave.EncoderException;
//import ws.schild.jave.MultimediaObject;
//import ws.schild.jave.encode.AudioAttributes;
//import ws.schild.jave.encode.EncodingAttributes;
//
//import java.io.File;
//
//class JaveTest{
//    public static void main(String[] args) {
//        encode(gen,"/tmp/sin.aac");
//    }
//    static void encode(String in,String out) throws EncoderException {
//        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm out.mp3 -y
//        //ffmpeg -f s16le -ar 44100 -ac 2 -c:a pcm_s16le -i theshow.pcm theshow.m4a -y
//        Encoder encoder = new Encoder();
//        for (String a : encoder.getAudioEncoders()) {
//            System.out.print(a+" ");
////            if(Objects.nonNull(a))throw new RuntimeException("cannot convert");
//        }
//        MultimediaObject mo = new MultimediaObject(new File(in));
//        EncodingAttributes att = new EncodingAttributes();
//        att.setInputFormat("pcm_u8");
//        att.setOutputFormat("aac");
//        AudioAttributes at = new AudioAttributes();
//        at.setBitRate(128000);
//        at.setChannels(2);
//        at.setSamplingRate(44100);
//        at.setCodec("aac");
////            audio.setSamplingRate(Math.min(MAX_SAMPLING_RATE_OF_AUDIO,samplingRateOfAudio));
//        att.setAudioAttributes(at);
//        encoder.encode(mo,new File(out),att);
//    }
//}
//
