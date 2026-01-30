package me.ccute.kvstore.server.storage;

import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.util.Map;

public class AOFHandler {
    private final File file;
    private DataOutputStream out;

    public AOFHandler(String filename) {
        this.file = new File(filename);
    }

    public void start() throws IOException {
        this.out = new DataOutputStream(new FileOutputStream(file, true));
    }

    public synchronized void logCommand(String commandName, String[] args) {
        try {
            out.writeUTF(commandName);
            out.writeInt(args.length);
            for (String arg : args) {
                out.writeUTF(arg);
            }
            out.flush();
        } catch (IOException e) {
            System.out.println(Logger.toLogMessage("Error while logging command " + commandName + "to file."));
            System.out.println(e.getMessage());
        }
    }

    public void recover(Map<String, String> db, Map<String, BaseCommand> commands) throws IOException {
        if (!file.exists()) return;
        System.out.println(Logger.toLogMessage("Recovering data from disk..."));

        int count = 0;
        try(DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            while (true) {
                String cmdName = in.readUTF();
                int argCount = in.readInt();
                String[] args = new String[argCount];
                for (int i = 0; i < argCount; i++) {
                    args[i] = in.readUTF();
                }

                BaseCommand cmd = commands.get(cmdName);
                if(cmd != null) {
                    // Replay the command execution
                    // out = null -> Do NOT send any network response.
                    // aof = null -> Do NOT log this command in the AOF again!
                    cmd.execute(db, args, null, null);
                    count++;
                }
            }
        } catch (EOFException e) {
            // End of file reached
        }
        System.out.println(Logger.toLogMessage("Recovery completed! Loaded " + count + " keys."));
    }

    public void close() throws IOException {
        if (out != null) out.close();
    }
}
