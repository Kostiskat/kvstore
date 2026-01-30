package me.ccute.kvstore.server;


import me.ccute.kvstore.server.commands.*;
import me.ccute.kvstore.server.storage.AOFHandler;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final ConcurrentHashMap<String, String> dbmap = new ConcurrentHashMap<>();
    private static final Map<String, BaseCommand> commands = new HashMap<>();

    // Init AOF handler
    private static final AOFHandler aof = new AOFHandler("db.aof");



    // STATIC block to register commands when the program starts
    static {
        commands.put("GET", new GetCommand());
        commands.put("SET", new SetCommand());
        commands.put("DEL", new DelCommand());
        commands.put("INCR", new IncrCommand());
    }

    static void main(String[] args) throws IOException {
        // The database's main hashmap
        String AOFPath = "data.db";

        try(ServerSocket server = new ServerSocket(6380)) {
            aof.start();
            aof.recover(dbmap, commands);
            aof.close();
            System.out.println(Logger.toLogMessage("Database running on port 6380."));
            while(true) {
                Socket client = server.accept();

                Thread worker = new Thread(() -> handleClient(client));
                worker.start();
                // Go to the next client, do not wait for the other one to disconnect.
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }





    private static void handleClient(Socket client) {
        try(DataInputStream in = new DataInputStream(client.getInputStream());
            DataOutputStream out = new DataOutputStream(client.getOutputStream())) {
            while (true) {
                String cmdtoken;
                try {
                    cmdtoken = in.readUTF().toUpperCase();
                } catch (EOFException e) {
                    System.out.println(Logger.toLogMessage("Client disconnected."));
                    break;
                }

                System.out.println(Logger.toLogMessage("@[Thread-" + Thread.currentThread().getId() + "] Received token: " + cmdtoken));
                BaseCommand cmd = commands.get(cmdtoken.toUpperCase());

                int argCount = in.readInt();
                String[] args = new String[argCount];
                for (int i = 0; i < argCount; i++) {
                    args[i] = in.readUTF();
                }

                if (cmd != null) {
                    cmd.execute(dbmap, args, out, aof);
                } else {
                    out.writeByte(0);
                    out.writeUTF("Unknown command: " + cmdtoken);
                }

                out.flush();
            }

        } catch (IOException e) {
            System.out.println(Logger.toLogMessage(e.getMessage() + "for client @" + Thread.currentThread()));
        }
    }
}
