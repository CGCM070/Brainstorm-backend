package org.brainstorm;

import java.util.UUID;

public class test {
    public static void main(String[] args) {
        String test = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println(test);
    }
}
