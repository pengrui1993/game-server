package media.ffmpeg;

import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avutil.AVChannelLayout;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.presets.avutil.AVERROR_EAGAIN;

public class EncodeAudio {
    static final PrintStream out = System.out;
    static AVCodecContext cc;
    static AVCodec codec;
    static AVFrame frame;
    static AVPacket pkt;
    static int i,j,k,ret;
    static float t;
    static OutputStream f;
    static void fp(AVFrame frame){
        ret = avcodec_send_frame(cc,frame);
        while(ret>=0){
            ret = avcodec_receive_packet(cc, pkt);//dts:-9223372036854775808,pts:-9223372036854775808,ration(0,1),dur:1152
            if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF)return;
            wrapper(()->{});//"Error encoding audio frame"
            //fwrite(pkt->data, 1, pkt->size, f);
            byte[] buf = new byte[pkt.size()];
            pkt.data().get(buf);
            try {
                f.write(buf);
            } catch (IOException e) {
                e.printStackTrace(out);
            }
            av_packet_unref(pkt);
        }
    }
    static int abs(int v){
        return Math.abs(v);
    }
    static int select_sample_rate(AVCodec codec){
        IntPointer p0 = codec.supported_samplerates();
        if(0==p0.get())return 44100;
        int best_sample_rate = 0;
        IntPointer p;
        for(int i=0;;i++){
            p=p0.getPointer(i);
            int v;
            if((v=p.get())==0)break;
            if(best_sample_rate!=0
                    ||abs(44100 - v) < abs(44100 - best_sample_rate)){
                best_sample_rate = v;
            }
        }
        return best_sample_rate;
    }
    static void check_sample_fmt(AVCodec codec,int sample_fmt){
        IntPointer p = codec.sample_fmts();
        for(int i=0;;i++){
            int v;
            if((v=p.get(i))==AV_SAMPLE_FMT_NONE)break;
            if(v==sample_fmt)return;
        }
        out.println("no supported sample format:"+sample_fmt);
        System.exit(0);
    }
    static int select_channel_layout( AVCodec codec, AVChannelLayout dst){
        AVChannelLayout p;
        if(Objects.isNull(p=codec.ch_layouts())){
//          AV_CHANNEL_LAYOUT_STEREO
            try(AVChannelLayout l = new AVChannelLayout()){
                return av_channel_layout_copy(dst,l.order(AV_CHANNEL_ORDER_NATIVE)
                        .nb_channels(2)
                        .u_mask(AV_CH_LAYOUT_STEREO)
                        .opaque(null));
            }
        }
        int i=0;
        AVChannelLayout ref = p.getPointer(0);
        AVChannelLayout best_ch_layout = ref;
        int best_nb_channels = 0;
        while(0!=ref.nb_channels()){
            int nb_channels = ref.nb_channels();
            if(nb_channels > best_nb_channels){
                best_ch_layout = ref;
                best_nb_channels = nb_channels;
            }
            ref = p.getPointer(++i);
        }
        return av_channel_layout_copy(dst,best_ch_layout);
    }
    static void wrapper(Runnable r){
        Optional.ofNullable(r).ifPresent(Runnable::run);
        if(ret<0){
            RuntimeException e = new RuntimeException();
            out.println(Arrays.stream(e.getStackTrace()).toList());
            System.exit(-1);
        }
    }
    static class Config{
        int cid;//AV_CODEC_ID_MP2
        int fmt;//AV_SAMPLE_FMT_S16
        int a;
        Config(int c,int f,int a){cid=c;fmt=f;this.a=a;}
    }
    static final List<Config> cs = List.of(
            new Config(AV_CODEC_ID_MP3,AV_SAMPLE_FMT_FLTP,1)//sometime error
            ,new Config(AV_CODEC_ID_MP2,AV_SAMPLE_FMT_S16,Short.MAX_VALUE/3)//no error
            ,new Config(AV_CODEC_ID_AAC,AV_SAMPLE_FMT_FLTP,1) //always error
    );
    public static int choice = 1;
    static Config cfg = cs.get(choice);

    /**
     * encode a sin wave to mp2 file
     *
     * play:
     * ffplay /tmp/java.out
     * info:
     * ffprobe /tmp/java.out
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
//        InputStream is = new ByteArrayInputStream(new byte[0]);
        Path des = Paths.get("/tmp/java.out");
        if(!Files.exists(des))Files.createFile(des);
        f = Files.newOutputStream(des);
        codec = avcodec_find_encoder(cfg.cid);
        cc = avcodec_alloc_context3(codec);
        cc.bit_rate(64000);
        cc.sample_fmt(cfg.fmt);
        check_sample_fmt(codec, cc.sample_fmt());
        cc.sample_rate(select_sample_rate(codec));
        wrapper(()->ret = select_channel_layout(codec, cc.ch_layout()));
        wrapper(()->ret = avcodec_open2(cc, codec, (AVDictionary) null));
        pkt = av_packet_alloc();
        frame = av_frame_alloc();
        frame.nb_samples(cc.frame_size());//1152
        frame.format(cc.sample_fmt());
        wrapper(()->ret = av_channel_layout_copy(frame.ch_layout(), cc.ch_layout()));
        wrapper(()->ret = av_frame_get_buffer(frame,0));//allocate the data buffers
        boolean cond =cfg.fmt==AV_SAMPLE_FMT_S16;
//        avformat_write_header
        for (t = 0,i = 0; i < 200; i++)
        {
            wrapper(()->ret = av_frame_make_writable(frame));
            final Pointer samples = cond?new ShortPointer(frame.data(0)):new FloatPointer(frame.data(0));//samples0 = /*(uint16_t *)*/frame.data(0);
            for (j = 0; j < cc.frame_size(); j++){
                double val = cfg.a*sin(2 * M_PI * 440.0 *t++/ cc.sample_rate());
                if(cond){
                    final long base_ptr = (long) cc.ch_layout().nb_channels() *j;
                    ((ShortPointer)samples).put( base_ptr,(short)val);
                    for (k = 1; k < cc.ch_layout().nb_channels(); k++){
                        ((ShortPointer)samples).put(base_ptr+k,((ShortPointer)samples).get(base_ptr));//samples[ch_n * j + k] = samples[ch_n * j];
                    }
                }else{
                    for (k = 0; k < cc.ch_layout().nb_channels(); k++){
                        ((FloatPointer)samples).put(j+ k * cc.frame_size(),(float)val);
                    }
                }
            }
            fp(frame);
        }
        fp(null);
        f.close();
        av_frame_free(frame);
        av_packet_free(pkt);
        avcodec_free_context(cc);
    }
    static double sin(double d){
        return Math.sin(d);
    }
}
