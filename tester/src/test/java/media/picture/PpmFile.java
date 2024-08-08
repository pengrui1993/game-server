package media.picture;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PpmFile {
    //ffplay /tmp/test.ppm
    public static void main(String[] args) throws IOException {
        Path f = Paths.get("/tmp/test.ppm");
        Files.deleteIfExists(f);
        Files.createFile(f);
        OutputStream os = Files.newOutputStream(f);
        String header = "P5\n%d %d\n%d\n";
        int x = 100;
        int y = 20;
        String title = String.format(header,x,y,255);
        os.write(title.getBytes(StandardCharsets.UTF_8));
        for(int r=0;r<y;r++){
            for(int c=0;c<x;c++){
                if(c<=3||c>=97){
                    os.write(0);
                }else{
                    os.write(255);
                }
            }
        }
        os.close();
    }
}
