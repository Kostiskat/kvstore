package me.ccute.kvstore.server.commands;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class GetCommand extends BaseCommand {
    public GetCommand() {
        super(1);
    }

    @Override
    protected void executeImpl(Map<String, String> db, String[] args, DataOutputStream out) throws IOException {
        // args[0] - key
        String key = args[0];
        String value = db.get(key);
        sendString(out, value);
    }
}
