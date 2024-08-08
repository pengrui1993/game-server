package media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.javacpp.BytePointer;

import java.util.Objects;
import java.util.Optional;

public class SuffixCodec {
    public static void main(String[] args) {
        show("a.mkv");
        show("a.avi");
        show("a.flv");
        show("a.mp4");
        show("a.mov");
        show("a.m4a");
        System.out.println(" ************ unknown ************");
        show("a.mp3");
        show("a.aiff");
        System.out.println(" ************ audio only **************");
        show("a.adts");//https://cloud.tencent.com/developer/article/2275391
        show("a.aac");
        show("a.ogg");
        show("a.wav");
        show("a.wma");
        System.out.println(Long.MIN_VALUE);
    }
    public static void show(String filename) {
        AVOutputFormat of = avformat.av_guess_format(null, filename, null);
        if(Objects.isNull(of)){
            System.out.println("unknown:"+filename);
            return;
        }
        AVCodec aec = avcodec.avcodec_find_encoder(of.audio_codec());
        AVCodec vec = avcodec.avcodec_find_encoder(of.video_codec());
        System.out.printf("filename:%s,ac:%s\tvc:%s\n"
                ,filename
                , Optional.ofNullable(aec).map(AVCodec::name).map(BytePointer::getString).orElse("nil")
                , Optional.ofNullable(vec).map(AVCodec::name).map(BytePointer::getString).orElse("nil")
        );
        of.close();
    }
}
