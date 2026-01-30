package me.ccute.kvstore.server.commands;

import me.ccute.kvstore.server.storage.AOFHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class IncrCommand extends BaseCommand{

    public IncrCommand() {
        super(1, "INCR <key> [incr]");
    }

    @Override
    protected void executeImpl(Map<String, String> db, String[] args, DataOutputStream out, AOFHandler aof) throws IOException {
        String key = args[0];
        long increment = 1;

        if (args.length > 1) {
            try {
                increment = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                sendError(out, "value is not an integer or out of range");
                return;
            }
        }


        String valStr = db.get(key);
        long value = 0; // Default value to write if key doesn't exist

        if (valStr != null) {
            try {
                value = Long.parseLong(valStr);
            } catch (NumberFormatException e) {
                sendError(out, "value is not an integer or out of range");
                return;
            }
        }

        value += increment;
        String newValue = String.valueOf(value);
        db.put(key, newValue);

        if (aof != null) {
            aof.logCommand("SET", new String[]{key, newValue});
        }

        sendString(out, "(integer) " + newValue);
    }
}
