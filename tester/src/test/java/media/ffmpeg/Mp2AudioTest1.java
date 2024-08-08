package media.ffmpeg;

import media.sounds.PlayerContext;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVChannelLayout;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.*;

import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class Mp2AudioTest1 {
    static final PrintStream out = System.out;
    static List<Integer> range(int from, int to){
        List<Integer> res = new ArrayList<>();
        for(int i=from;i<=to;i++)res.add(i);
        return res;
    }
    static List<Integer> range(int to){
        return range(0,to);
    }
    static PlayerContext pp = null;
    static boolean check_sample_fmt(AVCodec c, int sample_fmt)
    {
        IntPointer p = c.sample_fmts();
        int i=0;
        int v;
        while ((v=p.get(i++)) != AV_SAMPLE_FMT_NONE) if (v == sample_fmt) return true;
        return false;
    }
    static boolean select_channel_layout(AVCodec codec, AVChannelLayout dst){
        AVChannelLayout p;
        if(Objects.isNull(p=codec.ch_layouts())){
            try(AVChannelLayout l = new AVChannelLayout()){
                return 0==av_channel_layout_copy(dst,l.order(AV_CHANNEL_ORDER_NATIVE)
                        .nb_channels(2)
                        .u_mask(AV_CH_LAYOUT_STEREO)
                        .opaque(null));
            }
        }
        int i,nb_channels;
        AVChannelLayout ref;
        int best_nb_channels =i= 0;
        AVChannelLayout best_ch_layout = p.getPointer(0);
        while(0!=(nb_channels=(ref = p.getPointer(++i)).nb_channels())){
            if(nb_channels > best_nb_channels){
                best_ch_layout = ref;
                best_nb_channels = nb_channels;
            }
        }
        return 0==av_channel_layout_copy(dst,best_ch_layout);
    }
    static String smp_fmt_desc(int sample_fmt){
        String display_name;
        final boolean be = ByteOrder.nativeOrder()==ByteOrder.BIG_ENDIAN;
        switch (sample_fmt){
            case AV_SAMPLE_FMT_U8-> display_name = "u8";
            case AV_SAMPLE_FMT_S16-> display_name = be?"s16be":"s16le";
            case AV_SAMPLE_FMT_S32-> display_name = be?"s32be":"s32le";
            case AV_SAMPLE_FMT_FLT-> display_name = be?"f32be":"f32le";
            case AV_SAMPLE_FMT_DBL-> display_name = be?"f64be":"f64le";
            default -> display_name = "unknown";
        }
        return display_name;
    }
    static String fmt_name(int fmt){
        return av_get_sample_fmt_name(fmt).getString();
    }
    static int pkt_smp_fmt(int fmt){
        return av_get_packed_sample_fmt(fmt);
    }
    static String ch_ly_desc(AVChannelLayout cl){
        byte[] desc = new byte[512];
        int len = av_channel_layout_describe(cl, desc, desc.length);
        return new String(desc,0,len);
    }
    static void info2(AVCodecContext cc){
        boolean planar = 0!= av_sample_fmt_is_planar(cc.sample_fmt());
        int sample_fmt = cc.sample_fmt();
        out.println("origin sample fmt:"+fmt_name(sample_fmt));
        if(planar)out.println("packed sample fmt:"+fmt_name(sample_fmt = pkt_smp_fmt(sample_fmt)));
        out.printf("planar:%s,display name:%s,%s\n",planar, smp_fmt_desc(sample_fmt),ch_ly_desc(cc.ch_layout()));
//                    System.out.println(frm.ch_layout().order()==AV_CHANNEL_ORDER_NATIVE);//true
//                    System.out.println(frm.ch_layout().nb_channels());//2
//                    System.out.println(frm.nb_samples());//1152
//                    System.out.println(frm.pkt_size());//1253
//                    System.out.println(cc.frame_size());//1152
//                        System.out.println(cc.bit_rate());//384000
//                    System.out.println(frm.linesize(0));//2304
    }
    static void audio_info(int idx,AVFormatContext ctx){
        AVCodecParameters cp = ctx.streams(idx).codecpar();
        System.out.println(cp.bit_rate());//384000
        System.out.println(cp.ch_layout().nb_channels());//2
        System.out.println(cp.ch_layout().order());//1 AV_CHANNEL_ORDER_NATIVE
        System.out.println(cp.width());//0
        System.out.println(cp.height());//0
    }

    public static void sho_info(){
//        print_cc_info(AV_CODEC_ID_MP2,true);//Audio: mp2, 0 channels
//        print_cc_info(AV_CODEC_ID_MP2,false);//Audio: mp2, 0 channels, s16p, 128 kb/s
//        print_cc_info(AV_CODEC_ID_MP3,false);//Audio: mp3 (mp3float), 0 channels, fltp, 128 kb/s
        print_cc_info(AV_CODEC_ID_MP3,true);//Audio: mp3, 0 channels
    }
    static void print_cc_info(int cid,boolean encoder){
        int id = cid;
        AVCodec ac = encoder ?avcodec.avcodec_find_encoder(id):avcodec.avcodec_find_decoder(id);
        AVCodecContext acc = avcodec.avcodec_alloc_context3(ac);
        avcodec.avcodec_open2(acc,ac,(AVDictionary) null);
        print_codec_string(acc,true);
        avcodec.avcodec_close(acc);
        //
        IntPointer p = avcodec.avcodec_find_encoder(AV_CODEC_ID_MP3).sample_fmts();
        for(int i=0;AV_SAMPLE_FMT_NONE!= p.get(i);i++){
            out.println(av_get_sample_fmt_name(p.get(i)).getString());
        }
        AVCodec e = avcodec.avcodec_find_decoder(AV_CODEC_ID_MP3);
        System.out.println(e);
        out.println( avcodec.av_codec_is_decoder( e));
        out.println( avcodec.av_codec_is_encoder( avcodec.avcodec_find_encoder(AV_CODEC_ID_MP3)));
    }
    static void print_codec_string(AVCodecContext cc,boolean exit){
        byte[]buf0 = new byte[1024];
        // Stream #0:0: Audio: mp2, 44100 Hz, stereo, fltp, 384 kb/s  //by av_dump_format
        //              Audio: mp2, 44100 Hz, stereo, s16p, 384 kb/s
        avcodec.avcodec_string(buf0,buf0.length,cc,0);
        System.out.println(new String(buf0));
        if(exit)System.exit(0);
    }
    static boolean use_best = true;
    static int get_audio_index(boolean best,AVFormatContext fmt_ctx,AVCodec ac){
        if(best) return av_find_best_stream(fmt_ctx,AVMEDIA_TYPE_AUDIO,-1,0, ac,0);
        return range(fmt_ctx.nb_streams()-1)
                .stream()
                .filter(i->fmt_ctx.streams(i)
                        .codecpar()
                        .codec_type() == AVMEDIA_TYPE_AUDIO)
                .findFirst()
                .orElse(-1);
    }
    static void test_prob(){
        int ret;
        var s = new AVFormatContext(null);
        var c = new AVCodec(null);
        ret = avformat_open_input(s,vf_path,null,null);
        ret = avformat_find_stream_info(s,(AVDictionary) null);
        ret = av_find_best_stream(s,AVMEDIA_TYPE_AUDIO,-1,0,c,0);
        var st = s.streams(ret);
        AVCodecContext cc = avcodec.avcodec_alloc_context3(null);//create
        ret = avcodec.avcodec_parameters_to_context(cc,st.codecpar());//param
        ret = avcodec.avcodec_open2(cc,c,(AVDictionary) null);//init
        AVCodec c2 = find_probe_decoder(s, st);
        out.println(c2.long_name().getString());
        System.exit(0);
    }
    static AVCodec find_probe_decoder(AVFormatContext s,AVStream st){
        AVCodec probe_codec,codec;
        int codec_id = st.codecpar().codec_id();
        int t = st.codecpar().codec_type();
        if(t==AVMEDIA_TYPE_VIDEO)codec=s.video_codec();
        else if(t==AVMEDIA_TYPE_AUDIO)codec=s.audio_codec();
        else if (t==AVMEDIA_TYPE_SUBTITLE)codec=s.subtitle_codec();
        else codec=avcodec_find_decoder(codec_id);
        if(Objects.isNull(codec))return null;
        PointerPointer<AVCodec> iter = new PointerPointer<>(avutil.av_mallocz(Pointer.sizeof(PointerPointer.class)));
        while(Objects.nonNull(probe_codec = av_codec_iterate(iter))){
            if(probe_codec.id()==codec.id()
                &&0!=av_codec_is_decoder(probe_codec)
                && 0!=(probe_codec.capabilities()&(AV_CODEC_CAP_AVOID_PROBING | AV_CODEC_CAP_EXPERIMENTAL))
            )return probe_codec;
        }
        return codec;
    }
    static void detail(AVStream s){
//        var codec = find_probe_decoder(ic, s, s.codecpar().codec_id());
//        out.println( s.duration());//3326607354
//        out.println(s.r_frame_rate().num()+s.r_frame_rate().den());//0
//        out.println(s.time_base().num());//1
//        out.println(s.time_base().den());//14112000
//        out.println(s.duration()*1.f/s.time_base().den());//235.72897 Duration: 00:03:55.73
    }
    static int bytes_per_sample=0;
    static void start_player_if_need(AVCodecContext cc,AVFrame frm){
        if(Objects.isNull(pp)){
            info2(cc);
            bytes_per_sample = av_get_bytes_per_sample(cc.sample_fmt());// flt fltp:4, s16:2
            ByteOrder order = frm.ch_layout().order()==AV_CHANNEL_ORDER_NATIVE
                    ?ByteOrder.nativeOrder():ByteOrder.LITTLE_ENDIAN;
            pp = new PlayerContext(frm.sample_rate(),16,frm.ch_layout().nb_channels()
                    ,true,order==ByteOrder.BIG_ENDIAN);
            pp.start();//(frm.sample_rate()*frm.ch_layout().nb_channels()* bit_of_sample )
        }// ffplay /Users/pengrui/learn/ffmpeg/theshow.mp2
    }
    static double data_fetch(AVFrame frm,int n_ch,int nb_sample,int data_size){
        double data;
        switch (data_size){
            case 1->data= frm.data(n_ch).get(nb_sample);
            case 2->data= frm.data(n_ch).getPointer(ShortPointer.class).get(nb_sample);
            case 4-> data = switch (frm.format()) {
                case AV_SAMPLE_FMT_FLT
                , AV_SAMPLE_FMT_FLTP ->
                        frm.data(n_ch).getPointer(FloatPointer.class).get(nb_sample);
                default -> throw new RuntimeException();
            };
            case 8->data = switch (frm.format()) {
                case AV_SAMPLE_FMT_DBL
                , AV_SAMPLE_FMT_DBLP ->
                        frm.data(n_ch).getPointer(DoublePointer.class).get(nb_sample);
                default -> throw new RuntimeException();
            };
            default-> throw new RuntimeException();
        }
        return data;
    }
    static String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
//    static String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.aac"; // play some error
//    static String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.mp3"; // play some error
    //        String vf_path = "/Users/pengrui/learn/ffmpeg/audio.out";

    enum Trans{
        PRIMITIVE,POINTER,BUFFER,CMP
    }

    /**
     * high level api to play mp3 file
     * like that:
     * ffplay /Users/pengrui/learn/ffmpeg/theshow.mp2
     *
     * note mp3/aac file has some error ,later to care about that
     * @throws Exception
     */
    static void play() throws Exception{
        int ret,asi; AVCodec ac;AVStream as;
        AVFormatContext fmt_ctx = new AVFormatContext(null);
        ret = avformat_open_input(fmt_ctx, vf_path, null, null);
//        out.println(fmt_ctx.iformat().extensions().getString());//mp2,mp3,m2a,mpa
        ret = avformat_find_stream_info(fmt_ctx, (AVDictionary) null);// i dont know but without this function, sws_getContext does not work

        asi = get_audio_index(use_best,fmt_ctx,ac=new AVCodec(null));

        fmt_ctx.audio_codec(ac);
        detail(as=fmt_ctx.streams(asi));
        AVCodecContext cc = avcodec.avcodec_alloc_context3(null);//create
        ret = avcodec.avcodec_parameters_to_context(cc,as.codecpar());//param
        ret = avcodec.avcodec_open2(cc,ac,(AVDictionary) null);//init
        /*=
        AVCodecParserContext p = avcodec.av_parser_init(ac.id());
        avcodec.av_parser_close(p);
        */
        av_dump_format(fmt_ctx, 0, vf_path, 0);// Stream #0:0: Audio: mp2, 44100 Hz, stereo, fltp, 384 kb/s
        print_codec_string(cc,false);                               //              Audio: mp2, 44100 Hz, stereo, s16p, 384 kb/s
        AVPacket pkt = avcodec.av_packet_alloc();
        AVFrame frm = avutil.av_frame_alloc();
//        frm.nb_samples(cc.frame_size()); //auto fill by [ avcodec_receive_frame]
//        frm.format(cc.sample_fmt());
//        ret = av_channel_layout_copy(frm.ch_layout(), cc.ch_layout());
//        ret = av_frame_get_buffer(frm, 0);
//        int sample = frm.nb_samples();
        while(av_read_frame(fmt_ctx,pkt)>=0){ //av_write_frame
            if(asi!=pkt.stream_index()){
                avcodec.av_packet_unref(pkt);
                continue;
            }
            ret = avcodec.avcodec_send_packet(cc,pkt);
            if(ret<0){out.println("send pkt error");System.exit(-1);}
            while(ret>=0){
                ret = avcodec.avcodec_receive_frame(cc,frm);
//                if(sample!=frm.nb_samples()){}//true
                if(AVERROR_EAGAIN()==ret) continue;
                if(AVERROR_EOF==ret)break;
                start_player_if_need(cc,frm);
                int smp_bytes = av_get_bytes_per_sample(frm.format());//note mp2 is (mp3float)
                int n_ch = frm.ch_layout().nb_channels();
//                if(frm.ch_layout().order()==cc.ch_layout().order()){}//true
//                if(frm.ch_layout().nb_channels()==cc.ch_layout().nb_channels()){}//true
//                if(frm.nb_samples()!=cc.frame_size()){}//true
                int plane_size = smp_bytes*frm.nb_samples();
                ByteBuffer buf = ByteBuffer.allocate(n_ch*smp_bytes * frm.nb_samples());//2*2*1152
                ShortBuffer sh = buf.asShortBuffer();
//                    String s = smp_fmt_desc(frm.format());
                for(int i=0;i<frm.nb_samples();i++){
                    for(int ch=0;ch<n_ch;ch++){
//                            double data = data_fetch(frm,ch,i,data_size);
                        trans(Trans.POINTER,frm,sh,i,ch);
                        //fwrite(frame->data[ch] + smp_bytes*nb_sample, 1, smp_bytes, outfile);
                    }
                }
                pp.write(buf.array());
            }

        }
        System.in.read();
        pp.stop();
        avcodec.av_packet_free(pkt);
        avutil.av_frame_free(frm);
        avformat.avformat_close_input(fmt_ctx);
    }
    static short last_tmp = 0;
    static int count = 0;
    static void trans(Trans t,AVFrame frm,ShortBuffer sh,int n_sample,int n_ch){
        int smp_bytes = av_get_bytes_per_sample(frm.format());
        int i=n_sample;
        int ch = n_ch;
        switch (t){
            case PRIMITIVE -> {
                int offset = smp_bytes*i;
                byte add0 = frm.data(ch).get(offset);
                byte add1 = frm.data(ch).get(offset+1);
                short data = (short)((add0<<8)|add1);
                sh.put((short)data);//sh.put(n_ch*i+ch,(short)data);
            }
            case BUFFER -> {
                int byte_buf_size = smp_bytes*frm.nb_samples();
                short data = frm.data(ch)
                        .capacity(byte_buf_size)
                        .asBuffer()
                        .order(ByteOrder.BIG_ENDIAN)//NOTE see DirectByteBuffer#asShortBuffer()
                        .asShortBuffer()
                        .get(i);
                sh.put(data);//sh.put(n_ch*i+ch,(short)data);
            }
            case POINTER -> {
                enum Type{
                    T1,T2,EXAMPLE
                    ;
                    static Type random(){
                        final Type[] all = Type.values();
                        return all[ThreadLocalRandom.current().nextInt(all.length)];
                    }
                }
                short data;
                switch (Type.random()){
                    case EXAMPLE -> {
                        ByteBuffer bb = frm.data(ch)
                                .getPointer(ShortPointer.class, i)
                                .asByteBuffer();
                        data = (short)((bb.get(0)<<8)|bb.get(1));
                    }
                    case T1 -> data = frm.data(ch)
                            .getPointer(ShortPointer.class,i)
                            .asByteBuffer()
                            .order(ByteOrder.BIG_ENDIAN)
                            .asShortBuffer()
                            .get(0);
                    case T2 -> data = frm.data(ch)
                            .getPointer(ShortPointer.class)
                            .getPointer(i)
                            .asByteBuffer()
                            .order(ByteOrder.BIG_ENDIAN)//BIG_ENDIANDirectShortBufferS?:DirectShortBufferU
                            .asShortBuffer()
                            .get(0);
                    default -> throw new RuntimeException();
                }
                sh.put(data);
            }
            case CMP -> {
                int offset = smp_bytes*i;
                int byte_buf_size = smp_bytes*frm.nb_samples();
                ByteBuffer buffer = frm.data(ch).capacity(byte_buf_size).asBuffer();
                byte add0 = buffer.get(offset);
                byte add1 = buffer.get(offset+1);
                short val1 = (short)((add0<<8)|add1);
                short val2 = frm.data(ch).capacity(byte_buf_size).asBuffer().order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(i);
                double data = val2;
//                                frm.data(ch).capacity(byte_buf_size).asBuffer().get();
//                                System.out.println(frm.data(ch).capacity(byte_buf_size).asBuffer());//DirectByteBuffer
//                                System.exit(0);
                sh.put((short)val2);//sh.put(n_ch*i+ch,(short)data);
            }
        }
    }
    static float max = 0;

    public static void main(String[] args) throws Exception {
//        sho_info();
//        test_prob();


        play();
//        test_pcm();
    }

    /**
     * play pcm
     * like that:
     * ffplay -f s16le -ch_layout 2 -ar 44100 /Users/pengrui/learn/ffmpeg/theshow.pcm
     * @throws Exception
     */
    public static void test_pcm() throws Exception {
        RandomAccessFile f = new RandomAccessFile("/Users/pengrui/learn/ffmpeg/theshow.pcm","r");
        pp = new PlayerContext(44100,16,2,true,false);
        pp.start();//(frm.sample_rate()*frm.ch_layout().nb_channels()* bit_of_sample )
        byte[]buf = new byte[4096];
        int len;
        while(-1!=(len=f.read(buf))){
            pp.write(buf,len);
        }
        System.in.read();
        f.close();
        pp.stop();

        float v = Float.intBitsToFloat(10010);
        int i = Float.floatToIntBits(v);
        int i1 = Float.floatToRawIntBits(v);
    }
}
