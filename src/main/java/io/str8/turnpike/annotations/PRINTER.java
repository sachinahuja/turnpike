package io.str8.turnpike.annotations;

import javax.annotation.processing.Messager;

import static javax.tools.Diagnostic.Kind.NOTE;

public class PRINTER {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    private static final String LOG = "[MICROMSG] ";

    public static Messager messager;


    public static void println(String message){

        if(messager!=null)
            messager.printMessage(NOTE, message);
        else
            System.out.println(message);
    }

}
