package org.games;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * {@link org.games.event.EventUtils#classes}
 */
public class GsonTest {
    static class Bean{
        public String name = "hello";
        public int age = 13;
    }
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .create();

        System.out.println(gson.toJson(new Bean()));
    }
}
