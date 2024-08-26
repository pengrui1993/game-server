package org.games.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @see /resources/public/bytes.html
 */
@EnableScheduling
@RestController
public class ServerSideEventController implements Notifier{
    static final Logger log = LoggerFactory.getLogger(ServerSideEventController.class);
    PrintWriter out = new PrintWriter(new WriterAdapter());
    static class WriterAdapter extends PrintStream{
        public WriterAdapter() {
            super(new OutputStream() {
                StringWriter s = null;
                @Override
                public void write(int b) {
                    if(s==null){
                        s = new StringWriter();
                    }
                    s.write(b);
                }
                @Override
                public void flush() {
                    if(s!=null){
                        log.info(s.toString());
                        s = null;
                    }
                }
            });
        }
    }

    @Override
    public void sendTestEvent() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String msg = now.format(fmt);
        for (Monitor client : clients) {
            try {
                client.sse.send(msg);
            } catch (IOException e) {
                e.printStackTrace(out);
            }
        }
    }

    @Override
    public void info() {
        log.info("size of client:{}",clients.size());
    }

    void test(SseEmitter e){
        SseEmitter.SseEventBuilder b = SseEmitter.event();
        b.name("eventName").data("eventData", MediaType.TEXT_EVENT_STREAM);
        try {
            e.send(b);
        } catch (IOException ex) {
            ex.printStackTrace(out);
        }
    }
    Set<Monitor> clients = new HashSet<>();
    class Monitor{
        SseEmitter sse;
        long last;
        boolean dead = false;
        public Monitor(){
            clients.add(this);
        }
    }
    @Scheduled(cron = "0/10 * * * * ?")
    void scanner(){
        long n = now();
//        log.info("tick {},size:{}",n,clients.size());
        clients.removeAll(clients.stream().filter(e->e.dead).collect(Collectors.toSet()));
        for (Monitor client : clients) {
            if(n -client.last>=5000){
                client.dead = true;
            }
        }

    }
    @GetMapping("/api/http/sse/time")
    @CrossOrigin
    public SseEmitter time(){
        Monitor monitor = new Monitor();
        SseEmitter se = new SseEmitter(Long.MAX_VALUE);
        //Runnable rm = ()-> clients.remove(se);
        se.onTimeout(()->log.info("timeout"));
        se.onCompletion(()->log.info("completion"));
        se.onError(e->e.printStackTrace(out));
        monitor.sse = se;
        monitor.last = now();
        return se;
    }
    long now(){
        return System.currentTimeMillis();
    }
}
