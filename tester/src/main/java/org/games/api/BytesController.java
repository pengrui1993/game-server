package org.games.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @see /resources/public/bytes.html
 */
@RestController
public class BytesController {
    static final Logger log = LoggerFactory.getLogger(BytesController.class);
    @PostMapping("/bytes/action")
    public String action(@RequestBody byte[] data){
        log.info("action:"+data.length);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(data))){
            byte[] buf = new byte[1024];
            int len;
            while(-1!=(len=is.read(buf)))
                os.write(buf,0,len);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
            return "unknown";
        }
        String string = os.toString();
        log.info(string);
        return string;
    }

    public static void main(String[] args) throws IOException {
        String txt = "Hello World!";
        byte[] bytes = txt.getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        gos.write(bytes);
        gos.flush();
        gos.close();
        System.out.println(baos.toByteArray().length);//32
    }
}
