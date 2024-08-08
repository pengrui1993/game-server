package media.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVOutputFormat;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.presets.javacpp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Consumer;

/**
 <a href="https://github.com/Manevolent/ffmpeg4j/blob/master/src/test/java/FFmpegInputTest.java">...</a>
 */
public class FFmpegInfo {

    static void platform(){
        Loader.Detector.getPlatform();
        Class<javacpp> javacppClass = javacpp.class;
    }
    static void print(Collection<String> item){
        int i = 0;
        for (String msg:item) {
            i++;
            System.out.print(msg+"|");
            if(i==10){
                System.out.println();
                i = 0;
            }
        }
    }
    static void print(String sm, Consumer<Collection> c){
        try {
            Method m = FFmpeg.class.getDeclaredMethod(sm);
            m.setAccessible(true);
            Collection  invoke = (Collection)m.invoke(null);
            c.accept(invoke);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    static void changeCase(){
        char a = 'v';
        a ^= 'a'^'A';
        System.out.println(a+""+('A'-'a'));
    }
    public static void main(String[] args) throws FFmpegException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        changeCase();
        outer:
        while(true){
            System.out.println("d for de-muxers,m for muxers,c for codec");
            int read = System.in.read();
            switch (read){
                case 'd'-> print("iterateDemuxers",(c)-> print(((Collection<AVInputFormat>)c).stream().map(e->e.name().getString()).toList()));
                case 'm'-> print("iterateMuxers",(c)-> print(((Collection<AVOutputFormat>)c).stream().map(e->e.name().getString()).toList()));
                case 'c'-> print("iterateCodecs",(c)-> print(((Collection<AVCodec>)c).stream().map(e->e.name().getString()).toList()));
                case '\r','\n'->{}
                default -> {break outer;}
            }
        }

//        print(iterateCodecs().stream().map(e->e.name().getString()).toList());
//        print(iterateMuxers().stream().map(e->e.name().getString()).toList());//wsaud wtv wv yuv4mpegpipe
//        print(iterateDemuxers().stream().map(e->e.name().getString()).toList());
//        print(iterateDemuxers().stream().map(e->e.long_name().getString()).toList());

    }

}
