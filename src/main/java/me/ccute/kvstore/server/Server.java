package me.ccute.kvstore.server;


import me.ccute.kvstore.server.commands.BaseCommand;
import me.ccute.kvstore.server.commands.GetCommand;
import me.ccute.kvstore.server.commands.SetCommand;
import me.ccute.kvstore.server.utils.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final HashMap<String, String> dbmap = new HashMap<>();

    private static final Map<String, BaseCommand> commands = new HashMap<>();

    // STATIC block to register commands when the program starts
    static {
        commands.put("GET", new GetCommand());
        commands.put("SET", new SetCommand());
    }

    static void main(String[] args) throws IOException {
        // The database's main hashmap


        try(ServerSocket server = new ServerSocket(6380)) {
            System.out.println(Logger.toLogMessage("Database running on port 6380."));
            while(true) {
                Socket client = server.accept();
                handleClient(client);
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
                    cmdtoken = in.readUTF();
                } catch (EOFException e) {
                    System.out.println(Logger.toLogMessage("Client disconnected."));
                    break;
                }

                System.out.println(Logger.toLogMessage("Received token: " + cmdtoken));
                BaseCommand cmd = commands.get(cmdtoken);

                int argCount = in.readInt();
                String[] args = new String[argCount];
                for (int i = 0; i < argCount; i++) {
                    args[i] = in.readUTF();
                }

                if (cmd != null) {
                    cmd.execute(dbmap, args, out);
                } else {
                    out.writeByte(0);
                    out.writeUTF("Unknown command: " + cmdtoken);
                }

                out.flush();
            }

        } catch (IOException e) {
            System.out.println(Logger.toLogMessage("Connection error: " + e.getMessage()));
        }
    }
}
