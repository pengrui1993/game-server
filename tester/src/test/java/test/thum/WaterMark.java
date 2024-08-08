package test.thum;

import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static cn.hutool.core.img.ImgUtil.IMAGE_TYPE_JPG;

public class WaterMark {
    public static void watermark(File f) throws IOException {
        Thumbnails.Builder<File> srcFileBuilder = Thumbnails.of(f).scale(0.8f);
        var srcImg = srcFileBuilder.asBufferedImage();
        int width = (int)(srcImg.getWidth(null) * 0.15);
        int height = (int)(srcImg.getHeight(null) * 0.15);
        var cr = new ClassPathResource("classpath:water_mark.png");
        //加载水印图片
        Image pressImage = Img.from(cr).scale(width, height).getImg();
        BufferedImage bi = ImgUtil.castToBufferedImage(pressImage, IMAGE_TYPE_JPG);
        //贴水印
        srcFileBuilder.watermark(Positions.CENTER,bi, 0.3f) //位置，水印来源，透明度
                .outputQuality(0.6f).toFile(f);
    }
}
