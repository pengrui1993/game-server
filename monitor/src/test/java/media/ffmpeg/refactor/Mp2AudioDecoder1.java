package media.ffmpeg.refactor;

import media.ffmpeg.Mp2AudioTest1Refactor;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVFrame;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.javacpp.ShortPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;

import static org.bytedeco.ffmpeg.global.avutil.av_get_bytes_per_sample;
import static org.bytedeco.ffmpeg.global.avutil.av_q2d;

public class Mp2AudioDecoder1 extends Mp2AudioTest1Refactor {
    static double max;
    enum Trans{
        PRIMITIVE,POINTER,BUFFER,CMP
    }
    static void print_info(AVFormatContext fmt_ctx, AVPacket pkt, int audio_stream_index){
        if(pkt.dts()!=pkt.pts()){
            AVStream streams = fmt_ctx.streams(audio_stream_index);
            AVRational r = streams.time_base();
            double time = av_q2d(r);
            out.println("pkt.dur seconds:"+pkt.duration()*time);//0.026122448979591838
            out.println("pkt.dur timestamp:"+pkt.duration());//368640
            System.out.println("time:"+pkt.pts()*time);//0.9404081632653061
            out.println("dts:"+pkt.dts()+",pts:"+pkt.pts());//dts:13271040,pts:13271040
        }
    }
    static void trans(Trans t, AVFrame frm, ShortBuffer sh, int n_sample, int n_ch){
        int smp_bytes = av_get_bytes_per_sample(frm.format());
        int i=n_sample;
        int ch = n_ch;
        switch (t){
            case PRIMITIVE -> {
                int offset = smp_bytes*i;
                byte add0 = frm.data(ch).get(offset);
                byte add1 = frm.data(ch).get(offset+1);
                short data = (short)((add0<<8)|add1);
                sh.put(data);//sh.put(n_ch*i+ch,(short)data);
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
                            .order(ByteOrder.BIG_ENDIAN)//BIG_ENDIAN?DirectShortBufferS:DirectShortBufferU
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
}
