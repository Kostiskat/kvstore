package me.ccute.kvstore.server.commands;

import java.util.Map;

public class DelCommand extends BaseCommand {

    public DelCommand() {
        super(1, "DEL <key>");
    }

    @Override
    protected String executeImpl(Map<String, String> db, String[] args) {
        String key = args[0];

        String previousValue = db.remove(key);

        if (previousValue != null) {
            return "OK";
        } else {
            return "(nil)";
        }
    }
}