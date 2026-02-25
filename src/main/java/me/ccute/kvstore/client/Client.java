package me.ccute.kvstore.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

import static me.ccute.kvstore.client.parser.Tokenizer.fastTokenize;

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

                List<String> parts = fastTokenize(inputLine);
                String commandName = parts.getFirst();

                try {
                    byte[] cmdBytes = commandName.getBytes(StandardCharsets.UTF_8);
                    out.writeInt(cmdBytes.length);
                    out.write(cmdBytes);

                    out.writeInt(parts.size() - 1);

                    for (int i = 1; i < parts.size(); i++) {
                        byte[] argBytes = parts.get(i).getBytes(StandardCharsets.UTF_8);
                        out.writeInt(argBytes.length);
                        out.write(argBytes);
                    }
                    out.flush();

                    byte status = in.readByte();
                    int respLen = in.readInt();
                    byte[] respBytes = new byte[respLen];
                    in.readFully(respBytes);

                    String response = new String(respBytes, StandardCharsets.UTF_8);
                    System.out.println(status == 1 ? response : "(error): " + response);
                } catch (Exception e) {
                    System.err.println("Connection lost.");
                    e.printStackTrace();
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Connection failed! Is the server running?");
            throw new RuntimeException(e);
        }
    }
}