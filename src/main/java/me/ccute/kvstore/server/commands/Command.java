package me.ccute.kvstore.server.commands;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public interface Command {
    void execute(Map<String, String> db, String[] args, DataOutputStream out) throws IOException;
}
