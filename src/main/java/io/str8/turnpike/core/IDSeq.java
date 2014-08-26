package io.str8.turnpike.core;

public class IDSeq {

    private static int id = 0;

    public static int next(){
        return ++id;
    }
}
