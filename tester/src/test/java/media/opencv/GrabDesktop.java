package media.opencv;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;



public class GrabDesktop {
    static long now(){return System.currentTimeMillis();}
    // https://blog.csdn.net/leixiaohua1020/article/details/44597955

    /**
     * note: require some permission on mac os
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        desktop();
    }
    public static void desktop() throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("1");
        Dimension rect = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println(rect);
//        Rect rect = opencv_highgui.getWindowImageRect("0");
//        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("desktop");
//        grabber.setFormat("gdigrab");
        //ffmpeg -f avfoundation -i "1" out.mov // on macos
        int width = rect.width/4;
        int height = rect.height/4;
        grabber.setFormat("avfoundation");
        grabber.setOption("offset_x", "0");
        grabber.setOption("offset_y", "0");
        grabber.setOption("framerate", "25");
        grabber.setOption("draw_mouse", "0");
        grabber.setOption("video_size", String.format("%sx%s",width,height));
        // 这种形式，双屏有问题
        // grabber.setImageWidth(1920);
        // grabber.setImageWidth(1080);
        grabber.start();
        CanvasFrame canvas = new CanvasFrame("摄像头");
        canvas.setCanvasSize(width,height);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(false);
        while (canvas.isDisplayable()) {
            canvas.showImage(grabber.grab());
            TimeUnit.MILLISECONDS.sleep(40);
        }
        grabber.stop();
    }

}
