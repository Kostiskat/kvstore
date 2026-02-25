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
    public String execute(Map<String, String> db, String[] args, AOFHandler handler) throws IOException {
        if (args.length < minArguments) {
            return "illegal number of arguments (got: " + args.length + " expected: " + minArguments + ")\ncorrect syntax: " + syntax;
        }

        String result = executeImpl(db, args);

        if (handler != null) {
            handler.logCommand(this.getClass().getSimpleName().replace("Command", "").toUpperCase(), args);
        }

        return result;
    }

    protected abstract String executeImpl(Map<String, String> db, String[] args) throws IOException;


}
