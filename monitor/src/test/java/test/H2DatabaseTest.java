package test;
import java.sql.*;

import org.h2.server.web.DbStarter;
import org.h2.server.web.JakartaDbStarter;
import org.h2.tools.Server;
public class H2DatabaseTest {

    void test(){
        Class<DbStarter> dbStarterClass = DbStarter.class;
        Class<JakartaDbStarter> jakartaDbStarterClass = JakartaDbStarter.class;
    }
    static void registerGlobalExceptionHandler(){
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace(System.err);
            System.exit(-1);
        });
    }
    static Server server;
    static void startH2Server() throws SQLException {
        // start the TCP Server
        server = Server.createTcpServer().start();
        System.out.println(server.getPort());
    }
    static void startH2Console(String... args){
        Thread t = new Thread(()->{
            try {
                org.h2.tools.Console.main(args);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        t.setDaemon(true);
        t.start();
    }
    static void testSql() throws SQLException {
        Connection conn = DriverManager.
                getConnection("jdbc:h2:~/test", "sa", "");
        // add application code here
        Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        ResultSet set = statement.executeQuery("show databases");
//        set.beforeFirst();//move cursor to first
        ResultSetMetaData metaData = set.getMetaData();
        System.out.println(metaData);
        while(set.next()){
//            int row = set.getRow();//cursor row start by 1
            System.out.println(set.getString(1));
        }
        statement.close();
        conn.close();
    }
    public static void main(String[] args)
            throws Exception {
        startH2Server();
        startH2Console(args);
        testSql();
        System.in.read();
        server.stop();// stop the TCP Server
        System.exit(0);
    }
}
