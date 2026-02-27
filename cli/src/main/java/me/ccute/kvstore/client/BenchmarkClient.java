package me.ccute.kvstore.client;

import me.ccute.kvstore.sdk.client.KvClient;
import me.ccute.kvstore.sdk.client.KvClientBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BenchmarkClient {
    static void main() {
        int iterations = 50_000; // 50k SETs + 50k GETs = 100k total ops
        int pipelineSize = 100;  // Blast 100 at a time

        try (KvClient client = new KvClientBuilder().host("localhost").port(6379).build()) {

            System.out.println("Starting Async Pipelined Benchmark (50k SET + 50k GET)...");
            long startTime = System.nanoTime();

            for (int i = 0; i < iterations / pipelineSize; i++) {
                List<CompletableFuture<String>> futures = new ArrayList<>(pipelineSize * 2);

                for (int j = 0; j < pipelineSize; j++) {
                    String key = "bench:" + (i * pipelineSize + j);
                    futures.add(client.executeAsync("SET", key, "value" + j));
                    futures.add(client.executeAsync("GET", key));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }

            long endTime = System.nanoTime();
            double totalTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
            int totalOps = iterations * 2;
            double opsPerSec = totalOps / totalTimeSeconds;

            System.out.println("\n" + "=".repeat(30));
            System.out.println("    ASYNC PIPELINED RESULTS");
            System.out.println("=".repeat(30));
            System.out.printf("Total Commands: %d\n", totalOps);
            System.out.printf("Throughput    : %,.2f ops/sec\n", opsPerSec);
            System.out.printf("Avg Latency   : %,.4f ms/op\n", (totalTimeSeconds * 1000) / totalOps);
            System.out.println("=".repeat(30));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}