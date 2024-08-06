package media.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpeg;
import com.github.manevolent.ffmpeg4j.FFmpegError;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import com.github.manevolent.ffmpeg4j.FFmpegInput;
import com.github.manevolent.ffmpeg4j.source.FFmpegDecoderContext;
import com.github.manevolent.ffmpeg4j.source.MediaSourceSubstream;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avutil.AVRational;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avformat;
import org.bytedeco.ffmpeg.global.avutil;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Field;

public class FSourceStream extends FFmpegSourceStream {
    public FSourceStream(FInput input) {
        super(input);
    }
    Object getSuperField(String name){
        try {
            Field f = this.getClass().getSuperclass().getField(name);
            f.setAccessible(true);
            return f.get(this);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    void setSuperField(String name,Object value){
        try {
            Field f = this.getClass().getSuperclass().getField(name);
            f.setAccessible(true);
            f.set(this,value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Packet readPacket() throws IOException {
        try {
            while (true) {
                int result;
                AVPacket packet = avcodec.av_packet_alloc();


                Object readLock = getSuperField("readLock");
                FFmpegInput input = (FFmpegInput)getSuperField("input");
                // av_read_frame may not be thread safe
                synchronized (readLock) {
                    boolean registered = (Boolean)getSuperField("registered");
                    if (!registered) registerStreams();
                    for (; ; ) {
                        result = avformat.av_read_frame(input.getContext(), packet);
                        if (result != avutil.AVERROR_EAGAIN()) {
                            break;
                        }
                    }
                }
                try {
                    // Manual EOF checking here because an EOF is very important to the upper layers.
                    if (result == avutil.AVERROR_EOF) throw new EOFException("pos: " + getPosition() + "s");
                    else if (result == avutil.AVERROR_ENOMEM()) throw new OutOfMemoryError();

                    FFmpegError.checkError("av_read_frame", result);

                    // NOT USED: In case createdTime doesn't get set.
                    if ((packet.flags() & avcodec.AV_PKT_FLAG_KEY) == avcodec.AV_PKT_FLAG_KEY &&
                            getCreatedTime() <= 0D)
                        setCreatedTime(System.currentTimeMillis() / 1000D);

                    if ((packet.flags() & avcodec.AV_PKT_FLAG_CORRUPT) == avcodec.AV_PKT_FLAG_CORRUPT)
                        throw new IOException("read corrupt packet");

                    // Find the substream and its native context associated with this packet:
                    FFmpegDecoderContext substream = getSubstream(packet.stream_index());

                    // Handle any null contexts:
                    if (substream == null) continue;
                    int size = packet.size();
                    if (size <= 0) continue;

                    int finished =substream.isDecoding()?substream.decodePacket(packet):0;

                    AVRational timebase = getFormatContext().streams(packet.stream_index()).time_base();
                    double position = FFmpeg.timestampToSeconds(timebase, packet.pts());
                    setSuperField("position",position);
                    double duration = FFmpeg.timestampToSeconds(timebase, packet.duration());
                    return new Packet((MediaSourceSubstream) substream, size, finished, position, duration);
                } finally {
                    // VLC media player does this
                    avcodec.av_packet_unref(packet);
                }
            }
        } catch (FFmpegException ex) {
            throw new IOException(ex);
        }
    }
}
