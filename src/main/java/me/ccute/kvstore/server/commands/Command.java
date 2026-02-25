package me.ccute.kvstore.server.commands;

import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.IOException;
import java.util.Map;

public interface Command {
    String execute(Map<String, String> db, String[] args, AOFHandler handler) throws IOException;
}
