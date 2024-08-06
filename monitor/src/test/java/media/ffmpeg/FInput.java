package media.ffmpeg;

import com.github.manevolent.ffmpeg4j.FFmpegError;
import com.github.manevolent.ffmpeg4j.FFmpegException;
import com.github.manevolent.ffmpeg4j.FFmpegIO;
import com.github.manevolent.ffmpeg4j.FFmpegInput;
import com.github.manevolent.ffmpeg4j.stream.source.FFmpegSourceStream;
import org.bytedeco.ffmpeg.avformat.AVInputFormat;
import org.bytedeco.ffmpeg.avutil.AVDictionary;
import org.bytedeco.ffmpeg.global.avformat;

public class FInput extends FFmpegInput {
    public FInput(FFmpegIO io) {
        super(io);
    }
    protected boolean opened;
    @Override
    public FFmpegSourceStream open(AVInputFormat inputFormat) throws FFmpegException {
        FFmpegError.checkError("avformat_open_input",
                avformat.avformat_open_input(
                        getFormatContext(),
                        (String)null,
                        inputFormat,
                        null
                )
        );
        opened = true;
        FFmpegError.checkError(
                "avformat_find_stream_info",
                avformat.avformat_find_stream_info(getFormatContext(), (AVDictionary) null)
        );
        return new FSourceStream(this);
    }
    @Override
    public boolean isOpened() {
        return opened;
    }
    @Override
    public FFmpegSourceStream open(String format) throws FFmpegException {
        return super.open(format);
    }
}
