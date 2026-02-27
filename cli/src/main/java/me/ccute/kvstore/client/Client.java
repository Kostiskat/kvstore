package me.ccute.kvstore.client;

import me.ccute.kvstore.sdk.client.KvClient;
import me.ccute.kvstore.sdk.client.KvClientBuilder;
import me.ccute.kvstore.sdk.exceptions.KvException;

import java.util.List;
import java.util.Scanner;

import static me.ccute.kvstore.client.parser.Tokenizer.fastTokenize;

public class Client {
    // Note: ensure this is `public static void main(String[] args)` so Java can run it!
    static void main() {
        System.out.println("Connecting to database via SDK...");

        // 1. Use the Builder Pattern to create your client!
        // The try-with-resources block ensures client.close() is called when you exit.
        try (KvClient client = new KvClientBuilder().host("localhost").port(6379).build();
             Scanner userInput = new Scanner(System.in)) {

            System.out.println("Connected! Type a command (e.g. 'SET name Nick'):");
            System.out.println("Welcome to kvstore by ccute!");

            while (true) {
                System.out.print("> ");
                if (!userInput.hasNextLine()) break;
                String inputLine = userInput.nextLine().trim();

                if (inputLine.isEmpty()) continue;

                // Built-in exit command
                if (inputLine.equalsIgnoreCase("exit") || inputLine.equalsIgnoreCase("quit")) {
                    System.out.println("Bye!");
                    break;
                }

                List<String> parts = fastTokenize(inputLine);
                if (parts.isEmpty()) continue;

                String commandName = parts.getFirst();

                // Convert the rest of the list to an array for the arguments
                String[] cmdArgs = parts.subList(1, parts.size()).toArray(new String[0]);

                try {
                    // 2. Call the SDK! All byte mapping and networking is hidden inside.
                    String response = client.execute(commandName, cmdArgs);
                    System.out.println(response);

                } catch (KvException e) {
                    System.out.println("(error): " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Fatal error executing command: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Connection failed! Is the server running? Details: " + e.getMessage());
        }
    }
}