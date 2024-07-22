package test.thum;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

import java.io.IOException;
import java.io.File;
import java.util.Objects;

public class ThumImgTest {
    public static void main(String[] args) throws IOException {
        Thumbnails.of("hello.jpg").size(100,100).toFile("hello.png");
    }

    void test0() throws IOException {
        Thumbnails.of(Objects.requireNonNull(new File("path/to/directory").listFiles()))
                .size(640, 480)
                .outputFormat("jpg")
                .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
    }
}
