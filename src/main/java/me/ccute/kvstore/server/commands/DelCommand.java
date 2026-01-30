package me.ccute.kvstore.server.commands;

import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class DelCommand extends BaseCommand {

    public DelCommand() {
        super(1, "DEL <key>");
    }

    @Override
    protected void executeImpl(Map<String, String> db, String[] args, DataOutputStream out, AOFHandler aof) throws IOException {
        String key = args[0];

        String previousValue = db.remove(key);

        if (aof != null) {
            aof.logCommand("DEL", args);
        }
        if(previousValue != null) {
            sendOK(out);
        } else {
            sendString(out, "(nil)");
        }
    }
}
