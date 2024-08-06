package org.games;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
/*
https://cloud.tencent.com/developer/article/2067834
 */
public class NioServer {
    final int port;
    ServerSocketChannel server;
    Selector selector;
    SelectionKey serverKey;
    static final PrintStream out = System.out;
    long timeout;
    ServerHandler serverHandler;
    Map<SocketChannel,ClientHandler> clients = new HashMap<>();

    public NioServer(int port) {
        this.port = port;
        timeout = 10;
    }
    public void tick(){
        init();
        try {
//            selector.select();//blocking
            int select = selector.select(timeout);
            if(select<=0)return;
        } catch (IOException e) {
            return;
        }
        Set<SelectionKey> keys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while(iterator.hasNext()){
            SelectionKey key = iterator.next();
            Consumer.class.cast(key.attachment()).accept(key);
            iterator.remove();
        }
    }
    public void close(){
        try {
            selector.close();
        } catch (IOException e) {
        }
        try {
            server.close();
        } catch (IOException e) {
        }
    }
    private void init(){
        if(Objects.isNull(server)){
            try {
                server = ServerSocketChannel.open();
                server.configureBlocking(false);
                server.bind(new InetSocketAddress(port));
            } catch (IOException e) {
                return;
            }
        }
        if(server.isRegistered())return;
        if(Objects.isNull(selector)){
            try {
                selector = Selector.open();
            } catch (IOException e) {
                return;
            }
        }
        try {
            //SelectionKey.OP_ACCEPT==server.validOps()//true
            serverKey = server.register(selector, server.validOps(),serverHandler=new ServerHandler());
        } catch (ClosedChannelException e) {
            return;
        }
    }
    class ServerHandler implements Consumer<SelectionKey>{
        final NioServer server = NioServer.this;
        @Override
        public void accept(SelectionKey key) {
//            out.println(key==serverKey);//true
            SocketChannel accept;
            try {
                accept = server.server.accept();
                accept.configureBlocking(false);
            } catch (IOException e) {
                return;
            }
            try {
                ClientHandler handler;
                clients.put(accept,handler = new ClientHandler(accept));
                accept.register(selector,accept.validOps(),handler);
            } catch (ClosedChannelException e) {
                return;
            }
        }
    }
    class ClientHandler implements Consumer<SelectionKey>{
        final NioServer server = NioServer.this;
        final SocketChannel client;
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final ByteBuffer readBuf = ByteBuffer.allocate(1024);
        ByteBuffer writeBuf;
        public ClientHandler(SocketChannel accept) {
            client = accept;
        }
        @Override
        public void accept(SelectionKey key) {
            b.reset();
            if(key.isReadable()){
                readBuf.clear();
                try {
                    client.read(readBuf);
                } catch (IOException e) {
                    return;
                }
                readBuf.flip();
                if(0==(key.interestOps()&SelectionKey.OP_WRITE))
                    key.interestOpsOr(SelectionKey.OP_WRITE);
                out.println("client rcv:"+new String(readBuf.array(),0, readBuf.remaining()));
            }else if(key.isConnectable()){
                out.println("connectable");
            }else if(key.isWritable()){
                if(Objects.nonNull(writeBuf)){
                    int len = writeBuf.remaining();
                    while(true) {
                        try {
                            if (((len-client.write(writeBuf))>0)) yield1();
                        } catch (IOException e) {
                            break;
                        }
                    }
                    writeBuf = null;
                }
                key.interestOpsAnd(~SelectionKey.OP_WRITE);
                out.println("writeable");
            }else{
                out.println("no handler case");
            }
        }
    }
    public static void main(String[] args) {
        NioServer nioServer = new NioServer(1111);
        nioServer.tick();
        Client c = new Client().conn(1111);
        Scanner s = new Scanner(System.in);
        String line;
        while(true){
            nioServer.tick();
            try {
                if(System.in.available()>0){
                    line = s.nextLine().trim();
                    if("quit".equals(line))break;
                    System.out.println("console:"+c.write(line));
                }
            } catch (IOException e) {
            }
        }
        c.close();
        nioServer.close();
    }
    static void yield1(){
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Client{
    SocketChannel channel;
    Client(){
        try {
            channel = SocketChannel.open();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    Client conn(int port){
        try {
            boolean localhost = channel.connect(new InetSocketAddress("localhost", port));
        } catch (IOException e) {
            return null;
        }

        return this;
    }
    void close(){
        try {
            channel.close();
        } catch (IOException e) {
        }
    }
    int write(String msg){
        final ByteBuffer data = ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
        final int total = data.remaining();
        int len = total;
        int wc = 0;
        while(len>0){
            try {
                int l = channel.write(data);
                wc+=l;
                len-=l;
            } catch (IOException e) {
                return -1;
            }
        }
        assert wc==total;
        return total;
    }

}
