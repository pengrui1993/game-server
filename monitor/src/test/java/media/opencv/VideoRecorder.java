package media.opencv;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_highgui;
import org.bytedeco.opencv.global.opencv_video;
import org.bytedeco.opencv.global.opencv_videoio;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;

public class VideoRecorder implements Runnable {

    private static final int VIDEO_DEVICE_INDEX = 0;
    private FFmpegFrameRecorder recorder;
    private int width, height;
    public VideoRecorder(FFmpegFrameRecorder recorder, int width, int height) {
        this.recorder = recorder;
        this.width = width;
        this.height = height;
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        try {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(VIDEO_DEVICE_INDEX);
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.start();
            long startTS = 0, videoTS = 0;
            Frame frame;
            while (!Thread.interrupted() && (frame = grabber.grab()) != null) {
                if (startTS == 0) {
                    startTS = System.currentTimeMillis();
                }
                videoTS = 1000 * (System.currentTimeMillis() - startTS);
                if (videoTS > recorder.getTimestamp()) {
                    recorder.setTimestamp(videoTS);
                }
                recorder.record(frame);
            }

            grabber.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void test() throws FrameGrabber.Exception {
//        VideoCapture c = new VideoCapture();
//        double w = c.get(Videoio.CAP_PROP_FRAME_WIDTH);
//        double h = c.get(Videoio.CAP_PROP_FRAME_HEIGHT);
//        c.release();
//        c.close();

        OpenCVFrameGrabber g = new OpenCVFrameGrabber(1);
        g.start();
        while(g.getLengthInFrames()<1)yield1();
        Frame grab = g.grab();

        int w = grab.imageWidth;
        int h = grab.imageHeight;
        g.stop();
        g.close();
        Dimension rect = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(rect);
        System.out.println("w:"+w+",h:"+h);
    }
    public static void main(String[] args) throws FrameGrabber.Exception {
        test();
    }
}

