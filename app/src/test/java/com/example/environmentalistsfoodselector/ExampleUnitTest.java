package com.example.environmentalistsfoodselector;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private MainActivity app = new MainActivity();
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testExample0(){ assertEquals("Test Skeleton0 Code", app.skeleton0());}
    @Test
    public void testExample1(){ assertEquals("Test Skeleton0 Code", app.skeleton1());}
    @Test
    public void testExample2(){ assertEquals("Test Skeleton2 Code", app.skeleton2());}

    public void main(String[] test){

    }
}