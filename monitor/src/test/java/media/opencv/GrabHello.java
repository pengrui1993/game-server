package media.opencv;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class GrabHello {
    static long now(){return System.currentTimeMillis();}
    //ffmpeg -f avfoundation -i "<screen device index>:<audio device index>" output.mkv
    //ffmpeg -f avfoundation -i "1:0" output.mp4 -to 5
    //ffmpeg -f avfoundation -i ":0" test-output.aiff
    //ffmpeg -f avfoundation -i ":0" test-output.ogg
    public static void main(String[] args) throws Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.setImageWidth(1280);
        grabber.setImageHeight(720);
        grabber.start();
        CanvasFrame canvas = new CanvasFrame("摄像头");
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        long start = now();
        while (canvas.isDisplayable()) {
            if(now()-start>5000)break;
            canvas.showImage(grabber.grab());
            TimeUnit.MILLISECONDS.sleep(40);
        }
        grabber.stop();
        System.exit(0);
    }
}
