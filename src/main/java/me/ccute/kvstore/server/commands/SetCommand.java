package me.ccute.kvstore.server.commands;

import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class SetCommand extends BaseCommand {
    public SetCommand() {
        super(2, "DEL <key> <value>");
    }

    @Override
    protected void executeImpl(Map<String, String> db, String[] args, DataOutputStream out, AOFHandler aof) throws IOException {
        // args[0] - key
        // args[1] - value
        String key = args[0];
        String value = args[1];
        if (aof != null) {
            aof.logCommand("SET", args);
        }


        db.put(key, value);
        sendOK(out);
    }
}
