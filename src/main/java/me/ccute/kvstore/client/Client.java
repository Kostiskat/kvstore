package me.ccute.kvstore.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static void main() {
        System.out.println("Connecting to database...");


        try (Socket socket = new Socket("localhost", 6379);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             Scanner userInput = new Scanner(System.in)) {


            System.out.println("Connected! Type a command (e.g. 'SET name Nick'):");
            System.out.println("Welcome to kvstore by ccute!");

            while (true) {
                System.out.print("> ");
                String inputLine = userInput.nextLine().trim();

                if(inputLine.isEmpty()) continue;

                String[] parts = inputLine.split("\\s+");
                String commandName = parts[0];
                int argCount = parts.length - 1;

                try {
                    // -- SEND COMMAND --

                    out.writeUTF(commandName);
                    out.writeInt(argCount);

                    for(int i = 1; i < parts.length; i++) {
                        out.writeUTF(parts[i]);
                    }

                    out.flush();

                    // -- READ RESPONSE --
                    byte status = in.readByte();
                    String response = in.readUTF();

                    if (status == 1) {
                        System.out.println(response);
                    } else {
                        System.out.println("(error): " + response);
                    }
                } catch(IOException e) {
                    System.err.println("Error communicating with the server. Connection lost?");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Connection failed! Is the server running?");
            throw new RuntimeException(e);
        }
    }
}