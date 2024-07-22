package org.wolf.util;

import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TalkingRoom {
    @Override
    public String toString() {
        return "key:"+joinKey+",host:"+ip+",port:"+port;
    }
    DatagramChannel server;
    Selector selector;
    public final int port;
    public final String ip;
    float last;
    float lastAccessTime;
    Set<String> userId;
    String curUser;
    Map<String,SocketAddress> addsMap = new HashMap<>();
    ByteBuffer buf = ByteBuffer.allocate(512);
    SelectionKey serverKey;
    public final String joinKey= UUID.randomUUID().toString();
    byte[] bytes;
    public static TalkingRoom create(List<String> userIdList){
        return new TalkingRoom(){{
            userId = new HashSet<>(userIdList);
            bytes = joinKey.getBytes(StandardCharsets.UTF_8);
        }};
    }
    private TalkingRoom(){
        try {
            server = DatagramChannel.open().bind(null);
            server.configureBlocking(false);
            selector = Selector.open();
            serverKey = server.register(selector, SelectionKey.OP_READ);
            InetSocketAddress isa = InetSocketAddress.class.cast(server.getLocalAddress());
            port = isa.getPort();
            ip = isa.getAddress().getHostAddress();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean isTimeout(){
        return last-lastAccessTime>60*5;
    }
    public void active(String userId){
        if(this.userId.contains(userId))curUser = userId;
    }
    public void update(float dt){
        last+=dt;
        try {
            if(selector.selectNow()==0)return;
            Selector s = selector;
            Iterator<SelectionKey> itr = s.selectedKeys().iterator();
            DatagramChannel bind = server;
            while(itr.hasNext()){
                SelectionKey key = itr.next();
                if(key.isReadable()){
                    SocketAddress receive = bind.receive(buf.clear());
                    lastAccessTime = last;
                    buf.flip().mark();
                    Runnable broadcast = ()->{
                        if(!Objects.equals(addsMap.get(curUser),receive))return;//must be cur user send sounds
                        for (SocketAddress ad : addsMap.values()) {
                            buf.reset();
                            if(!Objects.equals(receive,ad)){
                                while(buf.hasRemaining()) {
                                    try {
                                        bind.send(buf,ad);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                        }
                    };
                    if(!isHello(buf)){
                        broadcast.run();
                        return;
                    }
                    String uid = new String(buf.array(), 5, buf.remaining() - 5);
                    if(userId.contains(uid)){
                        addsMap.put(uid,receive);
                        return;
                    }
                    broadcast.run();
                }else{
                    System.out.println("server unknown");
                }
                itr.remove();
            }
        } catch (Throwable e) {
            if(e instanceof ClosedSelectorException cs){
            }else{
            }
            e.printStackTrace(System.out);
        }
    }
    public void close(){
        Optional.ofNullable(server).ifPresent(s-> {
            try {
                selector.close();
            } catch (IOException ignored) {
            }
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    private boolean isHello(ByteBuffer buf){
        if(buf.remaining()<=bytes.length)return false;
        for(int i=0;i<buf.array().length;i++){
            if(bytes[i]!=buf.array()[i])return false;
        }
        return true;
    }
    public static void main(String[] args) throws IOException {
//        System.out.println("localhost:"+InetAddress.getLocalHost());
//        System.out.println("loop:"+InetAddress.getLoopbackAddress());
//        System.out.println("hostname:"+InetAddress.getLocalHost().getHostName());
//        System.out.println("address:"+InetAddress.getLocalHost().getHostAddress());
        AtomicBoolean running = new AtomicBoolean(true);
        List<InetSocketAddress> adds = new ArrayList<>();
        Queue<Thread> threads = new ConcurrentLinkedDeque<>();
        final PrintStream out = System.out;
        new Thread(()->{
            try {
                DatagramChannel bind = DatagramChannel.open().bind(new InetSocketAddress(0));
                SocketAddress local = bind.getLocalAddress();
                adds.add(InetSocketAddress.class.cast(local));
                bind.configureBlocking(false);
                Selector s = Selector.open();
//                System.out.println(local.getClass());//InetSocketAddress
//                System.out.println(local);//InetSocketAddress
//                System.out.println(bind.getRemoteAddress());//null
                ByteBuffer buf = ByteBuffer.allocate(512);
                Set<SocketAddress> target = new CopyOnWriteArraySet<>();
                SelectionKey serverKey = bind.register(s, SelectionKey.OP_READ);
                while(running.get()){
                    if(s.select(100)==0)continue;
                    Iterator<SelectionKey> itr = s.selectedKeys().iterator();
                    while(itr.hasNext()){
                        SelectionKey key = itr.next();
                        if(key.isReadable()){
                            SocketAddress receive = bind.receive(buf.clear());
                            buf.flip();
                            if(!buf.isDirect()){
                                if(buf.remaining()==4
                                    &&buf.array()[0]==0
                                    &&buf.array()[1]==0
                                    &&buf.array()[2]==0
                                    &&buf.array()[3]==0
                                ){
                                    target.add(receive);
//                                    System.out.println("server,local:"+bind.getLocalAddress());
//                                    System.out.println("server,remote:"+bind.getRemoteAddress());
                                    System.out.println("server,rcv hello,client:"+receive);
                                }else{
                                    buf.mark();
//                                    System.out.println("server side:"+receive);
//                                    System.out.println("server side:"+new String(buf.array(),0,buf.remaining()));
                                    for (SocketAddress ad : target) {
                                        bind.send(buf.reset(),ad);
                                    }
                                }
                            }
                        }else{
                            System.out.println("server unknown");
                        }
                        itr.remove();
                    }
                }
                bind.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("server quit done");
        }){{
            setDaemon(true);
            threads.offer(this);
        }}.start();
        class Client extends Thread{
            Client(){
                setDaemon(true);
                threads.offer(this);
            }
            final BlockingQueue<String> msg = new LinkedBlockingQueue<>();
            void add(String msg){
                this.msg.offer(msg);
            }
            @Override
            public void run() {
                while(adds.isEmpty())Thread.yield();
                InetSocketAddress server = adds.get(0);
                try {
                    DatagramChannel c = DatagramChannel.open().bind(null);
                    while(!c.connect(server).isConnected())Thread.yield();
//                    System.out.println("client side,local:"+c.getLocalAddress());
//                    System.out.println("client side,remote:"+c.getRemoteAddress());
                    c.send(ByteBuffer.wrap(new byte[]{0,0,0,0}),server);
                    DatagramPacket pkt = new DatagramPacket(new byte[512],512);
                    c.socket().setSoTimeout(10);
                    while(running.get()){
                            int len;
                            String take = msg.poll();
                            if(Objects.isNull(take)){
                                Thread.yield();
                            }else{
                                if("quit".equals(take))break;
                                ByteBuffer buf = ByteBuffer.wrap(take.getBytes(StandardCharsets.UTF_8));
                                len = c.send(buf.mark(),server);
                                out.println("client send len:"+len);
                            }
                            try{
                                c.socket().receive(pkt);
                                out.println("client "+ c.getLocalAddress()+" received:"+new String(pkt.getData(),pkt.getOffset(),pkt.getLength()));
                            }catch (SocketTimeoutException e){
                                Thread.yield();
                            }
//                        len = c.send(buf.reset(),server);
//                        System.out.println("client send len:"+len);
                    }
                    c.disconnect().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("client quit done");
            }
        }
        List<Client> clients = List.of(new Client(),new Client(),new Client());
        clients.forEach(Thread::start);
        Scanner scanner = new Scanner(System.in);
        ThreadLocalRandom r = ThreadLocalRandom.current();
        String line;
        while(running.get()){
            line = scanner.nextLine().trim();
            final String msg = line;
            switch (line){
                case "quit"-> running.set(false);
                case "help"->{}
                default ->clients.get(r.nextInt(clients.size())).add(msg);
            }
        }
        Thread thread;
        while(null!=(thread = threads.poll())){
            try {
                thread.join();
            } catch (InterruptedException ignored) {}
        }
    }
}
