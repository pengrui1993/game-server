package media.ffmpeg.net;

import media.sounds.PCMAudioPlayer;
import media.sounds.PCMPlayer;
import media.sounds.SoundPCMServer;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVCodecParserContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avutil.AVChannelLayout;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.DatagramChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.lang.annotation.ElementType.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
/*
mp2 header ff fd e0 04 99 66 66 77 66 88 77 66
*/

/**
 * {@link SoundPCMServer}
 */
public class MP2SoundServer {
    static final PrintStream out = System.out;
    static final String theshow = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
    static  String mp2File = theshow;
    static final AtomicBoolean running = new AtomicBoolean(true);
    static Path file = Paths.get("/tmp/queue.tmp");
    static AudioFormat fmt = new AudioFormat(44100,16,2,true,false);
    Thread lt;
    long last;
    DatagramChannel channel;
    ServerDecoder dec;
    OutputStream os;
    final Queue<Runnable> queue = new ConcurrentLinkedDeque<>();
    final Thread  queueWorker = new Thread(()->{
        try {
            Files.deleteIfExists(file);
            os = Files.newOutputStream(file
                    , StandardOpenOption.CREATE_NEW
//                    , StandardOpenOption.DELETE_ON_CLOSE
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while(running.get()){
            Runnable peek = queue.poll();
            if(Objects.nonNull(dec)&&now()-last>100)
                Utils.enqueue(queue,dec,null,0,os);
            if(null==peek){
                yield1();
                continue;
            }
            try{
                peek.run();
            }catch(Throwable t){
                t.printStackTrace(out);
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    });
    public MP2SoundServer(int port){
        queueWorker.setDaemon(true);
        queueWorker.start();
        try {
            channel = DatagramChannel.open();
            channel.bind(new InetSocketAddress(port));
            channel.socket().setSoTimeout(1000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        synchronized (MP2SoundServer.class){
            dec = new Decoder();
        }
    }
    static long now(){return System.currentTimeMillis();}
    public void run(){
        //ffplay -f s16le -ac 2 -ar 44100 theshow.pcm
        ByteBuffer buf = ByteBuffer.allocate(512);
        lt = new Player();
        lt.start();
        while(running.get()){
            try {
                buf.clear();
                SocketAddress receive = channel.receive(buf);
                buf.flip();
                last = now();
//                System.out.println(buf.limit()-buf.position());//576
                //decode buf packet to frame

                final byte[] b0;
                buf.get(b0= new byte[buf.remaining()]);
                Utils.enqueue(queue,dec,b0,b0.length,os);

//                    os.write(aos.buf(),0,aos.size());
            }catch (Throwable e) {
                if(e instanceof ClosedByInterruptException){
                    break;
                }
                System.out.println("timeout "+e);
            }
        }
        try {
            lt.join();
        } catch (InterruptedException e) {
            return;
        }
    }

    static void test_send(Consumer<byte[]> c){
        RandomAccessFile f;
        try {
            f = new RandomAccessFile(mp2File,"r");
            System.out.println("file.size:"+f.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int len;
        while(true){
            byte[]buf = new byte[512];
            try {
                len = f.read(buf);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if(len==-1)break;
            if(len!=buf.length)buf = Arrays.copyOf(buf, len);
            c.accept(buf);
            sleep(1);

        }
        try {
            f.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void sleep(long l){
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * client send mp2 file to server play that
     * like  {@link SoundPCMServer}
     * but add some decode step
     *
     * note1: that example decoding on server side
     * normal encode on client side and decode on other client side yet
     *
     * note2: that server side accept a packet enqueue packed data to worker
     * then backend worker thread decode(use low level ffmpeg api) mp2 pieces file (bunch up more than one packet)
     * and save pcm data into the 'queue.tmp' file
     * and other thread {@link Player}  play the music by reading the 'queue.tmp' pcm file
     *
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable {
        Thread server = new Thread(()->{ new MP2SoundServer(2222).run();});
        server.start();
        DatagramChannel channel = DatagramChannel.open();
        InetSocketAddress sc = new InetSocketAddress("localhost", 2222);
        channel.connect(sc);
        while(!channel.isConnected())yield1();
        test_send((buf)->{
            ByteBuffer wrap = ByteBuffer.wrap(buf);
            try {
                channel.send(wrap,sc);
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        });
        System.in.read();
        channel.close();
        running.set(false);
        server.interrupt();
        server.join();
    }
    static void play(){
        try {
            PCMPlayer.play(0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
class Decoder extends ServerDecoder{
    static final PrintStream out = System.out;
    static final int AUDIO_INBUF_SIZE = 20480;
    static final int BUF_SIZE = AV_INPUT_BUFFER_PADDING_SIZE+AUDIO_INBUF_SIZE;
    static final int AUDIO_REFILL_THRESH = 4096;
    int data_size = 0;
    int len;
    BytePointer out_ptr= new BytePointer((Pointer)null);
    IntPointer pkt_size = new IntPointer(1);
    BytePointer inbuf = new BytePointer(BUF_SIZE).capacity(BUF_SIZE);
    BytePointer data = new BytePointer(inbuf);
    static long now(){return System.currentTimeMillis();}
    int bytes;
    boolean decode_done;
    ByteArrayOutputStream bao= new ByteArrayOutputStream();
    ByteArrayInputStream bis = new ByteArrayInputStream(new byte[0]);
    @Override
    void decode(byte[] data0, int in_len, Consumer<AVFrame> c) {
        Runnable parser0 = ()->{
            len = av_parser_parse2(parser, cc, out_ptr,pkt_size,data, data_size, AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
            if(len<0){out.println("av_parser_parse2 error");System.exit(-1);}
            pkt.size(pkt_size.get());//0x13ec3d200
            pkt.data(out_ptr);
            data = data.getPointer(len);
            data_size -= len;
            if (pkt.size() > 0) {
                ret = avcodec.avcodec_send_packet(cc,pkt);
                show_info_if_error(ret);
                while(ret>=0){
                    ret = avcodec.avcodec_receive_frame(cc,frm);
                    if(AVERROR_EAGAIN()==ret||AVERROR_EOF==ret) {decode_done = true;return;}
                    c.accept(frm);
                }
            }
        };
        Consumer<Integer> read0 = (size)->{
            byte[]b1 = new byte[size];
            len = read(bis,b1);
            if (len > 0){
                data.getPointer(data_size)
                        .capacity(AUDIO_INBUF_SIZE - data_size)
                        .asBuffer()
                        .put(b1,0,len);
                data_size += len;
            }
        };
        if(null==data0){
            while(bis.available()>0){
                int min = Math.min(bis.available()
                        , AUDIO_INBUF_SIZE - data_size);
                read0.accept(min);
                parser0.run();
            }
            return;
        }
        bao.write(data0,0,in_len);
        boolean buffer_can_read = bis.available()+data_size+bao.size()+in_len>=AUDIO_INBUF_SIZE;
        if(!buffer_can_read)return;
        byte[] ir = new byte[bis.available()];
        read(bis,ir);
        byte[] br = bao.toByteArray();
        bao.reset(); write(bao,ir);write(bao,br);
        bis = new ByteArrayInputStream(bao.toByteArray());
        bao.reset();
        do { //av_write_frame
            int read_len = AUDIO_INBUF_SIZE - data_size;
            if(bis.available()<read_len)break;
            if (data_size < AUDIO_REFILL_THRESH) {
                mem_move(inbuf,data.address()-inbuf.address(),data_size);
                data = new BytePointer(inbuf);
                read0.accept(read_len);
            }
            parser0.run();
        } while (data_size > 0);
    }
    static int read(ByteArrayInputStream bio,byte[] buf){
        try {
            return bio.read(buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void write(ByteArrayOutputStream bao,byte[] b){
        try {
            bao.write(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    void old(byte[] data0, int in_len, Consumer<AVFrame> c){
        var cross =  (data.address()+data_size)+in_len>(inbuf.address()+AUDIO_INBUF_SIZE);
        if(cross){
            mem_move(inbuf,data.address()-inbuf.address(),data_size);
            data = new BytePointer(inbuf);
        }
        Runnable decode = ()->{
            while(data_size>0){
                len = av_parser_parse2(parser, cc, out_ptr,pkt_size,data, data_size, AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
                if(len<0){out.println("av_parser_parse2 error");System.exit(-1);}
                if(pkt_size.get()==0){decode_done = true;return;}
                pkt.size(pkt_size.get());//0x13ec3d200
                pkt.data(out_ptr);
                data = data.getPointer(len);
                data_size -= len;
                if (pkt.size() > 0) {
                    ret = avcodec.avcodec_send_packet(cc,pkt);
                    show_info_if_error(ret);
                    while(ret>=0){
                        ret = avcodec.avcodec_receive_frame(cc,frm);
                        if(AVERROR_EAGAIN()==ret||AVERROR_EOF==ret) {decode_done = true;return;}
                        c.accept(frm);
                    }
                }
            }
        };
        while (in_len > 0) {//-1 -3 -32 4 -103//bp.getPointer(-512).get(1)
            int cap = AUDIO_INBUF_SIZE - data_size;
            cap = Math.min(cap, in_len);
            BytePointer bp = data.getPointer(data_size).capacity(cap);
            ByteBuffer buffer = bp.asBuffer();
            for (int i = 0; i < cap; i++) buffer.put(data0[i]);
            int remain = in_len-cap;
            if(remain>0){
                byte[] nb = new byte[remain];
                System.arraycopy(data0, cap, nb, 0, in_len - cap);
                data0 = nb;
            }
            data_size += cap;
            in_len -= cap;

            decode.run();
        }
    }
    static void mem_move(BytePointer p,long start,int len){
        for(int i=0;i<len;i++)p.put(i,p.get(i+start));
    }
    static void show_info_if_error(int ret){
        if(ret<0){
            byte[] buf = new byte[128];
            int err = av_strerror(ret, buf, 128);//0
            out.println(err+",error on send packet,"+new String(buf));//Invalid data found when processing input
            System.exit(-1);
        }
    }
}

class ServerDecoder{
    AVCodec c;AVPacket pkt;AVFrame frm;
    AVCodecContext cc;
    AVCodecParserContext parser;
//            cc.sample_fmt(AV_SAMPLE_FMT_S16);
//            cc.sample_rate(44100);
    int ret;
    void init(){
        c = avcodec_find_decoder(AV_CODEC_ID_MP2);
        cc = avcodec_alloc_context3(c);
        ret = avcodec_open2(cc, c, (AVDictionary) null);
        parser = avcodec.av_parser_init(cc.codec_id());
        pkt = av_packet_alloc();
        frm = av_frame_alloc();
    }
    ServerDecoder(){init();}
    void decode(byte[] data,int len,Consumer<AVFrame>c){//no working
        ret = av_packet_make_writable(pkt);
        for(int i=0;i<len;i++){////-1 -11 -120 -4 119 85 68
            pkt.data().put(i,data[i]);
        }
        pkt.size(data.length);
        ret = avcodec.avcodec_send_packet(cc,pkt);
        while(ret>=0){
            ret = avcodec.avcodec_receive_frame(cc,frm);
            if(AVERROR_EAGAIN()==ret) continue;
            if(AVERROR_EOF==ret)break;
            if(ret<0)break;
            c.accept(frm);
        }
    }
}
class Player extends PCMAudioPlayer {
    public Player() {
        super( MP2SoundServer.fmt, MP2SoundServer.file, MP2SoundServer.running);
        log = false;
    }
}
class Utils{
    static void process(ServerDecoder dec
            , byte[]b0, int len,OutputStream os){
        dec.decode(b0,len,(frm)->{
            byte[]b1;
            int n_ch = frm.ch_layout().nb_channels();
            int smp_bytes = av_get_bytes_per_sample(frm.format());
            int fb_size = n_ch*smp_bytes * frm.nb_samples();
            ByteBuffer fb = ByteBuffer.allocate(fb_size);//2*2*1152
            ShortBuffer sh = fb.asShortBuffer();
            for(int i=0;i<frm.nb_samples();i++){
                for(int ch=0;ch<n_ch;ch++){
                    short data = new ShortPointer(frm.data(ch))
                            .getPointer(i)
                            .asByteBuffer()
                            .order(ByteOrder.BIG_ENDIAN)
                            .asShortBuffer()
                            .get(0);
                    sh.put(data);
                }
            }
            fb.get(b1 = new byte[fb_size]);
            try {
                os.write(b1);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        });
    }

    static void enqueue(Queue<Runnable> queue
            , ServerDecoder dec
            , byte[]b0, int len,OutputStream os){
        queue.offer(()-> process(dec,b0,len,os));
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
    static int select_channel_layout( AVCodec codec, AVChannelLayout dst){
        AVChannelLayout p;
        if(Objects.isNull(p=codec.ch_layouts())){
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
    static int abs(int v){
        return Math.abs(v);
    }
    static void test_send(Consumer<byte[]> c){
        DecodeFrameContext fc;
        EncodeFrameContext ec;
        synchronized (MP2SoundServer.class){
            fc = new DecodeFrameContext();
            ec = new EncodeFrameContext();
        }

        decode_and_encode_then_send(fc,ec,(pkt)->{
            byte[] buf;
            long pts = pkt.pts();
            long dts = pkt.dts();
            AVRational ra = pkt.time_base();
            ra.num();//0
            ra.den();//1
            long dur = pkt.duration();
            long pos = pkt.pos();
            pkt.data().get(buf=new byte[pkt.size()]);
            if(now()-last>1000){
                System.out.println(buf.length);//576
                last = now();
            }
            c.accept(buf);
        });
        fc.destroy();
        ec.destroy();
    }
    static long now(){return System.currentTimeMillis();}
    static long last=0;
    static void decode_and_encode_then_send(DecodeFrameContext dfc
            ,EncodeFrameContext efc,Consumer<AVPacket> c){
        while(dfc.noError()&&!dfc.eof()){
            if(!dfc.decode((frm)->{
                ByteBuffer buf = ByteBuffer.allocate(
                        frm.ch_layout().nb_channels()
                                *av_get_bytes_per_sample(frm.format())
                                * frm.nb_samples());
                ShortBuffer sh = buf.asShortBuffer();
                for(int i=0;i<frm.nb_samples();i++){
                    for(int ch=0;ch<frm.ch_layout().nb_channels();ch++){
                        int val = (frm.data(ch).get(i*2)<<8)
                                | frm.data(ch).get(i*2+1);
                        sh.put((short)val);
                    }
                }
                sh.flip();
                efc.encode(sh, c);
            })){
                System.out.println("decode_and_encode_then_send"+dfc.err);
            }
        }
        efc.fp(null,c);
    }
}
@Retention(RetentionPolicy.SOURCE)
@Target(value={CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, MODULE, PARAMETER, TYPE})
@interface Final{}
class DecodeFrameContext{
    @Final
    String file = MP2SoundServer.mp2File;
    AVFormatContext fc;
    AVCodecContext cc;
    AVCodec c;
    AVPacket pkt;
    AVFrame frm;
    int audio_stream_index;
    String err;
    boolean noError(){return Objects.isNull(err);}
    boolean eof(){ return eof;}
    boolean eof;
    DecodeFrameContext(){init();}
    boolean decode(Consumer<AVFrame> in){
        int ret = avformat.av_read_frame(fc,pkt);
        if(ret==AVERROR_EOF){
            eof = true;
            return false;
        }else if(ret<0){
            err = err_str(ret);
            return false;
        }
        if(audio_stream_index!=pkt.stream_index()){
            avcodec.av_packet_unref(pkt);
            return true;
        }
        if(avcodec.avcodec_send_packet(cc,pkt)<0){
            err = err_str(ret);
            destroy();
            return false;
        }
        do {
            if((ret = avcodec.avcodec_receive_frame(cc, frm)) == AVERROR_EAGAIN())
                continue;
            if (AVERROR_EOF == ret) {
                eof = true;
                return true;
            }
//                avcodec.av_packet_move_ref();
//                avutil.av_frame_move_ref(frm,frame);
            in.accept(frm);
        }while(ret>=0);

        return true;
    }
    String err_str(int ret){
        byte[]buf;
        return new String(av_make_error_string(buf=new byte[128],buf.length,ret));
    }
    void destroy(){
        if(Objects.nonNull(fc)) {avformat.avformat_free_context(fc);fc=null;}
        if(Objects.nonNull(cc)) {avcodec.avcodec_free_context(cc);cc=null;}
        if(Objects.nonNull(pkt)) {avcodec.av_packet_free(pkt);pkt=null;}
        if(Objects.nonNull(frm)) {avutil.av_frame_free(frm);frm=null;}
    }
    void init(){
        if(Objects.nonNull(fc))return;
        fc = new AVFormatContext(null);
        if(avformat.avformat_open_input(fc,file,null,null)<0){destroy();err="avformat_open_input";return;}
        if(avformat.avformat_find_stream_info(fc,(AVDictionary) null)<0){destroy();err="avformat_find_stream_info";return;}
        if((audio_stream_index=avformat.av_find_best_stream(fc,AVMEDIA_TYPE_AUDIO,-1,0,c=new AVCodec(null),0))<0){destroy();err="av_find_best_stream";return;}
        if(Objects.isNull(cc=avcodec.avcodec_alloc_context3(null))){destroy();err="avcodec_alloc_context3";return;}
        if(avcodec.avcodec_parameters_to_context(cc,fc.streams(audio_stream_index).codecpar())<0){destroy();err="avcodec_parameters_to_context";return;}
        if(avcodec.avcodec_open2(cc,c,(AVDictionary)null)<0){destroy();err="avcodec_open2";return;}
        if(Objects.isNull(pkt=avcodec.av_packet_alloc())){destroy();err="av_packet_alloc";return;}
        if(Objects.isNull(frm=avutil.av_frame_alloc())){destroy();err="av_frame_alloc";return;}
        avformat.av_dump_format(fc,audio_stream_index,file,0);
        err = null;
    }
}
class EncodeFrameContext{
    EncodeFrameContext(){init();}
    void encode(ShortBuffer buf,Consumer<AVPacket>c){
        ret = av_frame_make_writable(frm);
        ShortPointer sp = new ShortPointer(frm.data(0));
        for(int i=0;i<buf.limit()-buf.position();i++){
            sp.put(i,buf.get());
        }
        fp(frm,c);
    }
    void fp(AVFrame frm,Consumer<AVPacket>c){
        ret = avcodec.avcodec_send_frame(cc,frm);
        while(ret>=0){
            ret = avcodec_receive_packet(cc, pkt);
            if (ret == AVERROR_EAGAIN() || ret == AVERROR_EOF)return;
            c.accept(pkt);
            av_packet_unref(pkt);
        }
    }
    int smp_fmt = AV_SAMPLE_FMT_S16;
    int codec_id = AV_CODEC_ID_MP2;
    AVCodec c;
    AVCodecContext cc;
    AVPacket pkt;
    AVFrame frm;
    int ret;
    void init(){
        if(Objects.nonNull(c))return;
        c = avcodec.avcodec_find_encoder(codec_id);
        cc = avcodec.avcodec_alloc_context3(c);
        cc.bit_rate(64000);
        cc.sample_fmt(smp_fmt);
        cc.sample_rate(Utils.select_sample_rate(c));
        ret = Utils.select_channel_layout(c,cc.ch_layout());
        ret = avcodec.avcodec_open2(cc,c,(AVDictionary) null);
        pkt = avcodec.av_packet_alloc();
        frm = avutil.av_frame_alloc();
        frm.nb_samples(cc.frame_size());
        frm.format(cc.sample_fmt());
        ret = av_channel_layout_copy(frm.ch_layout(), cc.ch_layout());
        ret = av_frame_get_buffer(frm,0);
    }
    void destroy(){
        if(Objects.nonNull(cc)){avcodec.avcodec_free_context(cc);cc=null;}
        if(Objects.nonNull(pkt)){avcodec.av_packet_free(pkt);pkt=null;}
        if(Objects.nonNull(frm)){avutil.av_frame_free(frm);frm=null;}
    }
}

