package me.ccute.kvstore.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BenchmarkClient {
    static void main() {
        int iterations = 50_000; // 50k SETs + 50k GETs = 100k total ops
        int pipelineSize = 100;   // Batch size

        try (Socket socket = new Socket("localhost", 6379)) {
            socket.setTcpNoDelay(true);
            // Larger buffer for mixed traffic
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream(), 128 * 1024));
            DataInputStream in = new DataInputStream(socket.getInputStream());

            System.out.println("Starting Mixed Benchmark (50k SET + 50k GET)...");
            long startTime = System.nanoTime();

            for (int i = 0; i < iterations / pipelineSize; i++) {

                // 1. BLAST 100 SETs and 100 GETs
                for (int j = 0; j < pipelineSize; j++) {
                    String key = "bench:" + (i * pipelineSize + j);
                    writeCommand(out, "SET", key, "value" + j);
                    writeCommand(out, "GET", key);
                }
                out.flush();

                // 2. READ 200 Responses
                for (int j = 0; j < pipelineSize * 2; j++) {
                    in.readByte(); // status
                    int len = in.readInt();
                    in.skipBytes(len);
                }
            }

            long endTime = System.nanoTime();
            double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
            int totalOps = iterations * 2;
            double opsPerSec = totalOps / totalTimeSeconds;

            System.out.println("\n" + "=".repeat(30));
            System.out.println("    MIXED WORKLOAD RESULTS");
            System.out.println("=".repeat(30));
            System.out.printf("Total Commands: %d\n", totalOps);
            System.out.printf("Throughput    : %,.2f ops/sec\n", opsPerSec);
            System.out.printf("Avg Latency   : %,.4f ms/op\n", (totalTimeSeconds * 1000) / totalOps);
            System.out.println("=".repeat(30));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void writeCommand(DataOutputStream out, String cmdName, String... args) throws IOException {
        byte[] cmd = cmdName.getBytes(StandardCharsets.UTF_8);
        out.writeInt(cmd.length);
        out.write(cmd);
        out.writeInt(args.length);
        for (String arg : args) {
            byte[] bytes = arg.getBytes(StandardCharsets.UTF_8);
            out.writeInt(bytes.length);
            out.write(bytes);
        }
    }
}