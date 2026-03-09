package me.ccute.kvstore.server.commands;

import java.io.IOException;
import java.util.Map;

public class ExistsCommand extends BaseCommand {

    public ExistsCommand() {
        super(1, "EXISTS <key>");
    }

    @Override
    protected String executeImpl(Map<String, String> db, String[] args) {
        String key = args[0];

        if(db.containsKey(key)) {
            return "1";
        } else {
            return "0";
        }
    }
}
