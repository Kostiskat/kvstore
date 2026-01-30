package me.ccute.kvstore.server.netty;

public class Request {
    public String command;
    public String[] args;

    public Request(String cmd, String[] array) {
        this.command = cmd;
        this.args = array;
    }
}
