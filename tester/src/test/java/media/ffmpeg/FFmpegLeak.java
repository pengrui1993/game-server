package media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;

import java.io.IOException;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_alloc;
import static org.bytedeco.ffmpeg.global.avcodec.av_packet_free;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class FFmpegLeak {
    /**
     * correct action
     * no memory leak
     * @throws IOException
     */
    static void leak() throws IOException {
        for(int i=0;i<10000000;i++){
            AVFormatContext fc = avformat_alloc_context();
            avformat_free_context(fc);//must be free for no leak
        }
        AVPacket pkt = av_packet_alloc();
        for(int i=0;i<100000000;i++){
            PointerPointer<BytePointer> pop = new PointerPointer<>(pkt.data());//no memory leak
        }
        av_packet_free(pkt);
        for(int i=0;i<100000000;i++){
            BytePointer p = av_get_sample_fmt_name(AV_SAMPLE_FMT_S32);
        }
        for(int i=0;i<100000000;i++){
            avcodec.avcodec_find_decoder_by_name("mp2");
        }
        for(int i=0;i<100000000;i++){
            BytePointer p = new BytePointer(1);
            p.close();
        }
        for(int i=0;i<100000000;i++){
            Pointer p;
            PointerPointer<Pointer> ppp = new PointerPointer<>(p=av_malloc(1));
            av_free(p);
        }
        String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
        for(int i=0;i<100000000;i++) {
            AVFormatContext fmt_ctx = new AVFormatContext(null);
            int ret = avformat_open_input(fmt_ctx, vf_path, null, null);
//            avformat.avformat_free_context(fmt_ctx);//only call that some error happen must be call below function
            avformat_close_input(fmt_ctx);
        }
    }

    public static void main(String[] args) {

    }
}
