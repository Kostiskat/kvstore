package me.ccute.kvstore.server.utils;

public class Logger {
    public static String toLogMessage(String msg) {
        return "[kvstore] " + msg;
    }

    public static String invalidSyntaxMsg(String syntax) {return "Error(InvalidArgs) :: Usage: " + syntax;}

    public static String opExecuted(int ops) {return ops + " operations executed";}
}
