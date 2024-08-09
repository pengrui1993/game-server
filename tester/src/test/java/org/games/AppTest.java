package org.games;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    static void en(Class<?> e){
        if(e.isEnum()){
            for (Object f : e.getEnumConstants()) {
                Enum<?> g = (Enum<?>) f;
                System.out.println(g.name());
            }
        }
    }
    public static  void call(Class<?>... es) {
        for (var e : es) {
            en(e);
        }
    }

    public static void main(String[] args) {
        enum Type{
            ONE,TWO,THREE
        }
        enum Age{
            SMALL,BIG
        }
        call(Type.class,Age.class);
    }
}
