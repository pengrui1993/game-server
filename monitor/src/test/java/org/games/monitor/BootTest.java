package org.games.monitor;


import org.h2.tools.Server;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest()
public class BootTest {
    static Server server;
    @BeforeClass
    public static void setup() throws SQLException {
        server = Server.createTcpServer("-tcpAllowOthers").start();
        System.out.println("startup - creating DB connection");
    }
    @Before
    public void before() throws SQLException {
        System.out.println("DemoTest.before");
    }
    @Test
    public void doTest() throws IOException {
        int num = 1;
        Assert.assertEquals(num, 1);
    }
    @After
    public void after(){
        System.out.println("DemoTest.after");
    }
    @AfterClass
    public static void tearDown() {
        server.stop();
        System.out.println("closing DB connection");
    }
}