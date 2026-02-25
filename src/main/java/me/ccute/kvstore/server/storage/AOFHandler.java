package me.ccute.kvstore.server.storage;

import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
            byte[] cmdBytes = commandName.getBytes(StandardCharsets.UTF_8);
            out.writeInt(cmdBytes.length);
            out.write(cmdBytes);

            out.writeInt(args.length);

            for (String arg : args) {
                byte[] argBytes = arg.getBytes(StandardCharsets.UTF_8);
                out.writeInt(argBytes.length);
                out.write(argBytes);
            }
            out.flush();
        } catch (Exception e) {
            System.out.println(Logger.toLogMessage("Error logging command " + commandName));
        }
    }

    public void recover(Map<String, String> db, Map<String, BaseCommand> commands) throws IOException {
        if (!file.exists()) return;
        System.out.println(Logger.toLogMessage("Recovering data from disk..."));

        int count = 0;
        try(DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            // This isn't infinite, as it eventually reaches the EOFException
            while (true) {
               int cmdLen = in.readInt();
               byte[] cmdBytes = new byte[cmdLen];
               in.readFully(cmdBytes);
               String cmdName = new String(cmdBytes, StandardCharsets.UTF_8);

               int argCount = in.readInt();
               String[] args = new String[argCount];

               for (int i = 0; i < argCount; i++) {
                   int argLen = in.readInt();
                   byte[] argBytes = new byte[argLen];
                   in.readFully(argBytes);
                   args[i] = new String(argBytes, StandardCharsets.UTF_8);
               }

               BaseCommand cmd = commands.get(cmdName);
               if(cmd != null) {
                   cmd.execute(db, args, null);
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
