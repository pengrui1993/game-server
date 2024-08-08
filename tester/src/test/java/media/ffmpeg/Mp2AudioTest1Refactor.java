package media.ffmpeg;

import media.sounds.PlayerContext;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.*;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.bytedeco.ffmpeg.global.avcodec.AV_INPUT_BUFFER_PADDING_SIZE;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;

public class Mp2AudioTest1Refactor {
    protected static final PrintStream out = System.out;
//    static String vf_path = Mp2AudioTest1.vf_path;
    static String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
    static PlayerContext pp = null;
    static void start_player_if_need(AVFrame frm){
        if(Objects.isNull(pp)){
            pp = new PlayerContext(frm.sample_rate(),16,2
                    ,true,false);
            pp.start();//(frm.sample_rate()*frm.ch_layout().nb_channels()* bit_of_sample )
        }// ffplay /Users/pengrui/learn/ffmpeg/theshow.mp2
    }
    static boolean signed(int sample_format){
        return switch (sample_format){
            case AV_SAMPLE_FMT_U8
            ,AV_SAMPLE_FMT_U8P
                    -> false;
            case AV_SAMPLE_FMT_FLT
            ,AV_SAMPLE_FMT_FLTP
            ,AV_SAMPLE_FMT_DBL
            ,AV_SAMPLE_FMT_DBLP
            ,AV_SAMPLE_FMT_S16
            ,AV_SAMPLE_FMT_S16P
            ,AV_SAMPLE_FMT_S32
            ,AV_SAMPLE_FMT_S32P
            ,AV_SAMPLE_FMT_S64
            ,AV_SAMPLE_FMT_S64P
                    ->  true;
            default -> throw new RuntimeException();
        };
    }//frm.linesize(0):4608,frm.nb_samples():1152,av_get_bytes_per_sample(frm.format()):4

    enum FloatType{
        _1,_2,_3,NO_WORK,N_1
    }
    enum ShortType{
        _1,_2,NO_WORK
    }
    static FloatType ft = FloatType._1;
    static ShortType st = ShortType._1;
    static double data_fetch(AVFrame frm,int n_ch,int n_sample,int bytes_in_sample){
        final ByteOrder order = signed(frm.format())?ByteOrder.BIG_ENDIAN:ByteOrder.LITTLE_ENDIAN;
        return switch (bytes_in_sample){
            case 1->frm.data(n_ch).get(n_sample);
            case 2->switch (st){
                case _1 ->   (frm.data(n_ch).get(n_sample*2)<<8)
                            |(frm.data(n_ch).get(n_sample*2+1));

                case _2 -> frm.data(n_ch)
                        .getPointer(ShortPointer.class,n_sample)
                        .asByteBuffer()
                        .order(order)
                        .asShortBuffer()
                        .get(0);
                case NO_WORK -> frm.data(n_ch).getPointer(ShortPointer.class,n_sample).get(0);
            };
            case 4->switch (frm.format()) {
                case AV_SAMPLE_FMT_FLT //TODO
                , AV_SAMPLE_FMT_FLTP ->switch (ft){
                    case _1 -> Float.intBitsToFloat(
                             ((frm.data(n_ch).get(n_sample*4+3) <<24)   &0xff000000)
                            |((frm.data(n_ch).get(n_sample*4+2) <<16)   &0x00ff0000)
                            |((frm.data(n_ch).get(n_sample*4+1) <<8)    &0x0000ff00)
                            |((frm.data(n_ch).get(n_sample*4+0) <<0)    &0x000000ff)
                    )*10;
                    case _2 -> frm.data(n_ch)
                            .getPointer(FloatPointer.class,n_sample)
                            .get(0)*10;
                    case _3 ->frm.data(n_ch)
                            .getPointer(FloatPointer.class,n_sample)
                            .asByteBuffer()
                            .order(ByteOrder.LITTLE_ENDIAN)//WHY? see DirectByteBuffer#asFloatBuffer to get DirectFloatBufferU
                            .asFloatBuffer()
                            .get(0)*10;
                    case NO_WORK ->frm.data(n_ch)
                            .getPointer(FloatPointer.class,n_sample)
                            .asByteBuffer()
                            .order(order)// the order is failed
                            .asFloatBuffer()
                            .get(0)*10;
                    case N_1 -> Float.intBitsToFloat(
                            ((frm.data(n_ch).get(n_sample*4+0) <<24)   &0xff000000)
                                    |((frm.data(n_ch).get(n_sample*4+1) <<16)   &0x00ff0000)
                                    |((frm.data(n_ch).get(n_sample*4+2) <<8)    &0x0000ff00)
                                    |((frm.data(n_ch).get(n_sample*4+3) <<0)    &0x000000ff)
                    )*10
                            ;
                };
                default ->
                        frm.data(n_ch)
                        .getPointer(IntPointer.class,n_sample)
                        .asByteBuffer()
                        .order(order)
                        .asIntBuffer()
                        .get(0);
            };
            case 8->switch (frm.format()) {
                case AV_SAMPLE_FMT_DBL
                , AV_SAMPLE_FMT_DBLP ->frm.data(n_ch)
                        .getPointer(DoublePointer.class,n_sample)
                        .asByteBuffer()
                        .order(order)
                        .asDoubleBuffer()
                        .get(0);
                default -> frm.data(n_ch)
                        .getPointer(LongPointer.class,n_sample)
                        .asByteBuffer()
                        .order(order)
                        .asLongBuffer()
                        .get(0);
            };
            default-> throw new RuntimeException();
        };
    }
    static final Queue<byte[]> bq;
    static final Thread player;
    static{
        bq = new LinkedBlockingQueue<>();
        player = new Thread(()->{
            while(true){
                if(Objects.isNull(pp)){
                    yield1();
                    continue;
                }
                byte[] poll = bq.poll();
                if(null==poll){
                    yield1();
                    continue;
                }
                pp.write(poll);
            }
        });
        player.setDaemon(true);
        player.start();
    }
    int val = AV_INPUT_BUFFER_PADDING_SIZE;

    /**
     * play mp2 audio
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        int ret,asi; AVCodec ac;
        AVFormatContext fmt_ctx = new AVFormatContext(null);
        ret = avformat_open_input(fmt_ctx, vf_path, null, null);
        ret = avformat_find_stream_info(fmt_ctx, (AVDictionary) null);// i dont know but without this function, sws_getContext does not work
        asi = av_find_best_stream(fmt_ctx,AVMEDIA_TYPE_AUDIO,-1,0, ac=new AVCodec(null),0);
//        ac = avcodec_find_decoder(fmt_ctx.streams(asi).codecpar().codec_id());
        AVCodecContext cc = avcodec.avcodec_alloc_context3(null);//create
        ret = avcodec.avcodec_parameters_to_context(cc,fmt_ctx.streams(asi).codecpar());//param
        ret = avcodec.avcodec_open2(cc,ac,(AVDictionary) null);//init
        av_dump_format(fmt_ctx, 0, vf_path, 0);// Stream #0:0: Audio: mp2, 44100 Hz, stereo, fltp, 384 kb/s
        AVPacket pkt = avcodec.av_packet_alloc();
        AVFrame frm = avutil.av_frame_alloc();
        while(av_read_frame(fmt_ctx,pkt)>=0){ //av_write_frame
            if(asi!=pkt.stream_index()){
                avcodec.av_packet_unref(pkt);
                continue;
            }
            out.printf("dur:%s,pts:%s,dts:%s,pos:%s,si:%s\n"
                    ,pkt.duration(),pkt.pts(),pkt.dts(),pkt.pos(),pkt.stream_index()
                    ,pkt.size()
            );
            ret = avcodec.avcodec_send_packet(cc,pkt);
            if(ret<0){out.println("send pkt error");System.exit(-1);}
//            print_info(fmt_ctx,pkt,asi);
            while(ret>=0){
                ret = avcodec.avcodec_receive_frame(cc,frm);
                if(AVERROR_EAGAIN()==ret) continue;
                if(AVERROR_EOF==ret)break;
                start_player_if_need(frm);
                int smp_bytes = av_get_bytes_per_sample(frm.format());
                int n_ch = frm.ch_layout().nb_channels();
                int one_channel_size_in_bytes = smp_bytes*frm.nb_samples();
                ByteBuffer buf = ByteBuffer.allocate(n_ch*smp_bytes * frm.nb_samples());//2*2*1152
                ShortBuffer sh = buf.asShortBuffer();
                for(int i=0;i<frm.nb_samples();i++){
                    for(int ch=0;ch<n_ch;ch++){
                        double data = data_fetch(frm,ch,i,smp_bytes);
                        sh.put((short)data);

//                        trans(Trans.POINTER,frm,sh,i,ch);
                        //fwrite(frame->data[ch] + smp_bytes*nb_sample, 1, smp_bytes, outfile);
                    }
                }
                bq.offer(buf.array());
            }

        }
        System.in.read();
        pp.stop();
        avcodec.avcodec_close(cc);
        avcodec.av_packet_free(pkt);
        avutil.av_frame_free(frm);
        avformat.avformat_close_input(fmt_ctx);
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
