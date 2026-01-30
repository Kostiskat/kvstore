package me.ccute.kvstore.server.commands;

import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.*;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private final int minArguments;
    private final String syntax;

    public BaseCommand(int minArguments, String syntax) {
        this.minArguments = minArguments;
        this.syntax = syntax;
    }

    @Override
    public void execute(Map<String, String> db, String[] args, DataOutputStream out, AOFHandler handler) throws IOException {
        if (args.length < minArguments) {
            sendError(out, "illegal number of arguments (got: " + args.length + " expected: " + minArguments + ")\ncorrect syntax: " + syntax);
            return;
        }
        executeImpl(db, args, out, handler);
    }

    protected abstract void executeImpl(Map<String, String> db, String[] args, DataOutputStream out, AOFHandler aof) throws IOException;

    protected void sendOK(DataOutputStream out) throws IOException {
        if (out == null) return; // We are in recovery mode, do not send anything back.
        out.writeByte(1);
        out.writeUTF("operation completed.");
    }

    protected void sendString(DataOutputStream out, String msg) throws IOException {
        if (out == null) return;
        out.writeByte(1);
        out.writeUTF(msg);
    }

    protected void sendError(DataOutputStream out, String errMsg) throws IOException {
        if (out == null) return; // Would never happen, but safe to check anyway.
        out.writeByte(0);
        out.writeUTF(errMsg);
    }
}
