package me.ccute.kvstore.server.commands;

import java.util.Map;

public class SetCommand extends BaseCommand {
    public SetCommand() {
        super(2, "SET <key> <value>");
    }

    @Override
    protected String executeImpl(Map<String, String> db, String[] args) {
        String key = args[0];
        String value = args[1];

        db.put(key, value);

        return "OK";
    }
}