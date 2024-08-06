package media.ffmpeg;
import com.github.manevolent.ffmpeg4j.*;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import com.github.manevolent.ffmpeg4j.source.AudioSourceSubstream;
import com.github.manevolent.ffmpeg4j.source.FFmpegAudioSourceSubstream;
import com.github.manevolent.ffmpeg4j.source.MediaSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import org.bytedeco.ffmpeg.avcodec.AVCodec;
import org.bytedeco.ffmpeg.avcodec.AVCodecParameters;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avcodec.AVPacketSideData;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avformat.AVStream;
import org.bytedeco.ffmpeg.avutil.AVChannelLayout;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.IntPointer;
import media.sounds.PlayerContext;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

import static org.bytedeco.ffmpeg.global.avformat.avformat_close_input;
import static org.bytedeco.ffmpeg.global.avutil.*;
/*
planer and packet:
https://www.cnblogs.com/wangguchangqing/p/5851490.html
 */
public class F4jOggAudioTest {
    static final PrintStream out = System.out;
    void test(InputStream is) throws Exception {
        FFmpegInput fi = FFmpegIO.openInputStream(is);
        avformat.avformat_free_context(fi.getFormatContext());
        avformat.avformat_close_input(fi.getFormatContext());
        fi.close();
    }
    static PlayerContext pp = null;
    static void playOgg() throws Exception{
        InputStream is = ClassLoader.getSystemResourceAsStream("test.ogg");
        assert null!=is;
        FFmpegInput fi = FFmpegIO.openInputStream(is);
        FFmpegSourceStream stream = fi.open((AVInputFormat)null);
        stream.registerStreams();
        FFmpegAudioSourceSubstream fass = null;
        for (MediaSourceSubstream substream : stream.getSubstreams()) {
            if (substream.getMediaType() != MediaType.AUDIO)  continue;
            fass = (FFmpegAudioSourceSubstream) substream;
            break;
        }
        if (fass == null) throw new NullPointerException();
        AVCodec ac = fass.getCodecContext().codec();
        AudioFrame frame;
        ByteOrder o= null;
        int min = 4096;
        while (true) {
            try {
//                test(fass);
                frame = fass.next();
                AudioFormat format = frame.getFormat();
                if(Objects.isNull(pp)){
                    assert null!=ac;//AV_SAMPLE_FMT_S16
                    int bit = 16;
                    boolean signed = true;
                    AVChannelLayout layout = ac.ch_layouts();
                    o = layout.order()==avutil.AV_CHANNEL_ORDER_NATIVE
                            ? ByteOrder.nativeOrder():ByteOrder.LITTLE_ENDIAN;
                    pp = new PlayerContext(format.getSampleRate(),bit,format.getChannels(),signed,o==ByteOrder.BIG_ENDIAN);
                    pp.start();
                }
//                long channelLayout = format.getChannelLayout();//AVCodecParameters#channel_layout()
                float[] fb = frame.getSamples();
                assert null!=o;
                ByteBuffer buf = ByteBuffer.allocate(fb.length*(Short.SIZE/Byte.SIZE)).order(o);
                min = Math.min(buf.capacity(),min);//512
                for (float v : fb) {
                    buf.putShort((short) (Short.MAX_VALUE * v));
                }
                buf.flip();
                pp.write(buf.array());
            } catch (EOFException ex) {
                break;
            }
        }
        pp.stop();
        if(Objects.nonNull(ac)){
            ac.close();
        }
        fi.close();
    }

    public static void main(String[] args) throws Exception {
        playOgg();
    }
    static void codec(FFmpegInput fi){
        AVChannelLayout layout = null;
        for (int i = 0; i < fi.getContext().nb_streams(); i++) {
            AVFormatContext context = fi.getContext();
            AVStream streams = context.streams(i);
            if(avutil.AVMEDIA_TYPE_AUDIO!=streams.codecpar().codec_type())continue;
            AVCodecParameters params = streams.codecpar();
            layout = params.ch_layout();
            out.println(layout.nb_channels());
        }
    }
    static void test(FFmpegAudioSourceSubstream fass){
        FFmpegSourceStream parent = (FFmpegSourceStream)fass.getParent();
        AVPacket packet = avcodec.av_packet_alloc();
//        fass.decodePacket()
        AVPacketSideData avPacketSideData = packet.side_data();
        packet.close();
    }
    static void phase1(FFmpegInput fi){
        AVFormatContext fc = fi.getFormatContext();
        AVCodec c = new AVCodec();
        out.println(c.name());
        int index = avformat.av_find_best_stream(fc,avutil.AVMEDIA_TYPE_AUDIO,-1,-1,c,0);
        AVCodec ac = c;
        for(int i=0;i<fc.nb_streams();i++){
            AVStream as = fc.streams(i);
            AVCodecParameters p = as.codecpar();
            if(p.codec_type()!=avutil.AVMEDIA_TYPE_AUDIO)continue;
            ac = avcodec.avcodec_find_decoder(p.codec_id());
            System.out.println(ac.long_name().getString());
            IntPointer ip = ac.sample_fmts();
            switch (ip.get()){
                case AV_SAMPLE_FMT_FLTP-> System.out.println(AV_SAMPLE_FMT_FLTP+" float planar");
                case AV_SAMPLE_FMT_FLT-> System.out.println(AV_SAMPLE_FMT_FLT+" float packet");
            }
            out.println(av_get_sample_fmt_name(ip.get()).getString()//fltp
                    +",sample bytes:"+av_get_bytes_per_sample(ip.get()));//4
            out.println("name is fltp:"+(av_get_sample_fmt("fltp")==ip.get()));//true
            out.println("buf "+av_samples_get_buffer_size((int[])null,ac.ch_layouts().nb_channels()
                    ,44100,avutil.AV_SAMPLE_FMT_FLT,0));
            if(av_sample_fmt_is_planar(ip.get())==1){//true
                out.println("planer to packet:"+(av_get_packed_sample_fmt(ip.get())==AV_SAMPLE_FMT_FLT));
            }else{
                out.println("packet to planer:"+(av_get_planar_sample_fmt(ip.get())==AV_SAMPLE_FMT_FLTP));
            }
            break;
        }
    }









    public static void example() throws FFmpegException,IOException {
//        InputStream is = new FileInputStream("example.ogg");
        InputStream is = ClassLoader.getSystemResourceAsStream("test.ogg");
        assert null!=is;
        FFmpegInput fi = FFmpegIO.openInputStream(is);
        FFmpegSourceStream stream = fi.open((AVInputFormat)null);
        // Read the file header, and register substreams in FFmpeg4j
        stream.registerStreams();
        AudioSourceSubstream audioSourceSubstream = null;
        for (MediaSourceSubstream substream : stream.getSubstreams()) {
            if (substream.getMediaType() != MediaType.AUDIO) {
                out.println(substream.getMediaType());
                continue;
            }
            audioSourceSubstream = (AudioSourceSubstream) substream;
        }

        if (audioSourceSubstream == null) throw new NullPointerException();

        AudioFrame frame;

        while (true) {
            try {
                frame = audioSourceSubstream.next();
                AudioFormat format = frame.getFormat();
                int channels = format.getChannels();
                int sampleRate = format.getSampleRate();
                float[] interleaved_ABABAB_AudioSamples = frame.getSamples();
            } catch (EOFException ex) {
                break;
            }
        }
    }

}
