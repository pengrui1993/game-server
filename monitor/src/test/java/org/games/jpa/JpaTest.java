package org.games.jpa;


import jakarta.annotation.Resource;
import org.h2.tools.Server;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JpaTest {
    @BeforeClass
    public static void setup() throws SQLException {
        System.out.println("startup - creating jpa testing");
    }
    @Before
    public void before() throws SQLException {
        System.out.println("JpaTest.before");
    }
    //http://127.0.1.1:8082/
    @Resource
    private App app;
    @Resource
    private ConfigurableApplicationContext ctx;
    @Test
    public void doTest() throws IOException {
        int num = 1;
        System.out.println(app);
        System.out.println(ctx);
        Assert.assertEquals(num, 1);
    }
    @After
    public void after(){
        System.out.println("JpaTest.after");
    }
    @AfterClass
    public static void tearDown() {
        System.out.println("closing ");
    }
}