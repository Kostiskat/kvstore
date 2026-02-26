package me.ccute.kvstore.server.storage;

import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class AOFHandler {
    private final File file;
    private DataOutputStream out;

    // Holds up to 100,000 commands before writing to disk.
    private final LinkedBlockingQueue<LogEntry> queue = new LinkedBlockingQueue<>(100000);
    private volatile boolean running = true;



    public AOFHandler(String filename) {
        this.file = new File(filename);
    }

    public void start() throws IOException {
        this.out = new DataOutputStream(new FileOutputStream(file, true));

        Thread writerThread = new Thread(this::processQueue, "AOF-Background-Writer");
        writerThread.setDaemon(true); // Ensures the JVM can exit if this is the only thread left
        writerThread.start();
    }

    public void logCommand(String commandName, String[] args) {
        if (!running) return;

        boolean accepted = queue.offer(new LogEntry(commandName, args));
        if (!accepted) {
            System.err.println(Logger.toLogMessage("WARNING! AOF Queue is full! Dropping log for " + commandName));
        }
    }

    private void processQueue() {
        while (running || !queue.isEmpty()) {
            try {
                LogEntry entry = queue.take();

                do {
                    writeEntryToDisk(entry);
                } while ((entry = queue.poll()) != null);

                out.flush();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.err.println(Logger.toLogMessage("Fatal error writing to AOF: " + e.getMessage()));
            }
        }
    }

    private void writeEntryToDisk(LogEntry entry) throws IOException {
        byte[] cmdBytes = entry.commandName.getBytes(StandardCharsets.UTF_8);
        out.writeInt(cmdBytes.length);
        out.write(cmdBytes);

        out.writeInt(entry.args.length);

        for (String arg : entry.args) {
            byte[] argBytes = arg.getBytes(StandardCharsets.UTF_8);
            out.writeInt(argBytes.length);
            out.write(argBytes);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void recover(Map<String, String> db, Map<String, BaseCommand> commands) throws IOException {
        if (!file.exists()) return;
        System.out.println(Logger.toLogMessage("Recovering data from disk..."));

        int count = 0;
        try(DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            // The SuppressWarning annotation refers to this loop, which supposedly never finishes, but exits at EOFException when the file ends.
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
            // End of file reached : this is the point where the while loop ends.
        }
        System.out.println(Logger.toLogMessage("Recovery completed! Loaded " + count + " keys."));
    }

    public void close() throws IOException {
        running = false;
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        if (out != null) out.close();
    }

    private record LogEntry(String commandName, String[] args) {}
}
