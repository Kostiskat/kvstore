package me.ccute.kvstore.server.commands;

import java.io.*;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private final int minArguments;

    public BaseCommand(int minArguments) {
        this.minArguments = minArguments;
    }

    @Override
    public void execute(Map<String, String> db, String[] args, DataOutputStream out) throws IOException {
        if (args.length != minArguments) {
            sendError(out, "illegal number of arguments (got: " + args.length + " expected: " + minArguments + ")");
            return;
        }
        executeImpl(db, args, out);
    }

    protected abstract void executeImpl(Map<String, String> db, String[] args, DataOutputStream out) throws IOException;

    protected void sendOK(DataOutputStream out) throws IOException {
        out.writeByte(1);
        out.writeUTF("operation completed.");
    }

    protected void sendString(DataOutputStream out, String msg) throws IOException {
        out.writeByte(1);
        out.writeUTF(msg);
    }

    protected void sendError(DataOutputStream out, String errMsg) throws IOException {
        out.writeByte(0);
        out.writeUTF(errMsg);
    }
}
