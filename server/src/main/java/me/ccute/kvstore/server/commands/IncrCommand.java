package me.ccute.kvstore.server.commands;

import java.util.Map;

public class IncrCommand extends BaseCommand {

    public IncrCommand() {
        super(1, "INCR <key> [increment]");
    }

    @Override
    protected String executeImpl(Map<String, String> db, String[] args) {
        String key = args[0];
        long increment = 1;

        if (args.length > 1) {
            try {
                increment = Long.parseLong(args[1]);
            } catch (NumberFormatException e) {
                return "(error) ERR value is not an integer or out of range";
            }
        }

        String valStr = db.get(key);
        long currentValue = 0;

        if (valStr != null) {
            try {
                currentValue = Long.parseLong(valStr);
            } catch (NumberFormatException e) {
                return "(error) ERR value is not an integer or out of range";
            }
        }

        long newValue = currentValue + increment;
        String newValueStr = String.valueOf(newValue);

        db.put(key, newValueStr);

        return "(integer) " + newValueStr;
    }
}