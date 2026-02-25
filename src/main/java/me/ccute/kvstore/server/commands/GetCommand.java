package me.ccute.kvstore.server.commands;

import java.util.Map;
import java.util.Objects;

public class GetCommand extends BaseCommand {
    public GetCommand() {
        super(1, "GET <key>");
    }

    @Override
    protected String executeImpl(Map<String, String> db, String[] args) {
        String key = args[0];
        String value = db.get(key);

        return Objects.requireNonNullElse(value, "(nil)");
    }
}