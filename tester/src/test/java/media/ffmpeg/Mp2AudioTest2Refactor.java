package media.ffmpeg;

import media.sounds.PlayerContext;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecContext;
import org.bytedeco.ffmpeg.avcodec.AVCodecParserContext;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.ShortPointer;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Objects;

import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.presets.avutil.AVERROR_EAGAIN;

public class Mp2AudioTest2Refactor extends Mp2AudioTest1 {
    static final int AUDIO_INBUF_SIZE = 20480;
    static final int BUF_SIZE = AV_INPUT_BUFFER_PADDING_SIZE+AUDIO_INBUF_SIZE;
    static final int AUDIO_REFILL_THRESH = 4096;
    static AVCodecParserContext parser;
    static RandomAccessFile f;
    static int ret;
    static void pass(AVCodecContext cc,AVFrame frame,ByteBuffer buf){
        int n_ch = cc.ch_layout().nb_channels();//2
        ShortBuffer sh = buf.asShortBuffer();
        for (int i = 0; i < frame.nb_samples(); i++){
            for (int ch = 0; ch < n_ch; ch++){
                short data = new ShortPointer(frame.data(ch))
                        .getPointer(i)
                        .asByteBuffer()
                        .order(ByteOrder.BIG_ENDIAN)
                        .asShortBuffer()
                        .get(0);
                sh.put(data);
                // fwrite(frame->data[ch] + data_size * i, 1, data_size, outfile);
            }
        }
    }
    static void decode(AVCodecContext dec_ctx, AVPacket pkt, AVFrame frame){
        AVCodecContext cc = dec_ctx; AVFrame frm = frame;
        int ret, data_size;
        ret = avcodec.avcodec_send_packet(cc,pkt);
        if(ret<0){
            byte[] buf = new byte[128];
            int err = av_strerror(ret, buf, 128);//0
            out.println(err+",error on send packet,"+new String(buf));//Invalid data found when processing input
            System.exit(-1);
        }
        while(ret>=0){
            ret = avcodec.avcodec_receive_frame(cc,frm);
            if(AVERROR_EAGAIN()==ret||AVERROR_EOF==ret) return;
            data_size = av_get_bytes_per_sample(dec_ctx.sample_fmt());//2
            if(data_size<0){ out.println("decode error ,av_get_bytes_per_sample");System.exit(-1);}
            if(Objects.isNull(pp)){
                info2(cc);
                pp = new PlayerContext(frm.sample_rate(),16,2
                        ,true,false);
                pp.start();
            }
            int n_ch = dec_ctx.ch_layout().nb_channels();
            ByteBuffer buf = ByteBuffer.allocate(n_ch*data_size * frm.nb_samples());
            pass(dec_ctx,frame,buf);
            pp.write(buf.array());
        }
    }
    static void mem_move(BytePointer p,long start,int len){
        for(int i=0;i<len;i++)p.put(i,p.get(i+start));
    }
    public static void main(String[] args) throws Exception {
       ok();
    }

    /**
     * play mp2 use low level api of ffmpeg
     * @throws Exception
     */
    public static void ok() throws Exception {
        String vf_path = "/Users/pengrui/learn/ffmpeg/theshow.mp2";
        f = new RandomAccessFile(vf_path,"r");
        AVPacket pkt = avcodec.av_packet_alloc();
        AVFrame frm = avutil.av_frame_alloc();
        AVCodec ac = avcodec_find_decoder(AV_CODEC_ID_MP2);
        AVCodecContext cc = avcodec_alloc_context3(ac);
        ret = avcodec.avcodec_open2(cc,ac,(AVDictionary) null);//init
        parser = avcodec.av_parser_init(cc.codec_id());
        int data_size = 0;
        int len;
        BytePointer out_ptr= new BytePointer((Pointer)null);
        IntPointer pkt_size = new IntPointer(1);
        BytePointer inbuf = new BytePointer(BUF_SIZE).capacity(BUF_SIZE);
        BytePointer data = new BytePointer(inbuf);
        do { //av_write_frame
            if (data_size < AUDIO_REFILL_THRESH) {//-5 -34 -4 -114
                mem_move(inbuf,data.address()-inbuf.address(),data_size);
                data = new BytePointer(inbuf);
                len = f.getChannel().read(data.getPointer(data_size).capacity(AUDIO_INBUF_SIZE - data_size).asBuffer());
                if (len > 0)data_size += len;
            }
            len = av_parser_parse2(parser, cc, out_ptr,pkt_size,data, data_size, AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
            if(len<0){out.println("av_parser_parse2 error");System.exit(-1);}
            pkt.size(pkt_size.get());//0x13ec3d200
            pkt.data(out_ptr);
            data = data.getPointer(len);
            data_size -= len;
            if (pkt.size() > 0) decode(cc, pkt, frm);
        } while (data_size > 0);
        pkt.size(0);
        pkt.data(null);
        decode(cc, pkt, frm);
        inbuf.close();
        out_ptr.close();
        pkt_size.close();
        System.in.read();
        f.close();
        avcodec.av_packet_free(pkt);
        avutil.av_frame_free(frm);
        pp.stop();
    }
}
