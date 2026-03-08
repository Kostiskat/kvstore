# Home

<p align="center">
  <img src="docs/assets/kvstore_logo.svg" width="400" />
</p>

<p align="center">
  <img src="https://img.shields.io/github/actions/workflow/status/Kostiskat/kvstore/docker-image.yml?branch=main&style=for-the-badge&logo=github" />
  <img src="https://img.shields.io/badge/Java-25+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/github/license/Kostiskat/kvstore?style=for-the-badge&color=brightgreen" />
</p>

### Welcome to kvstore Documentation!

kvstore is a high-throughput, asynchronous, in-memory key-value database built from scratch on top of Netty. It is designed to handle 90,000+ operations per second with sub-millisecond latency, featuring a custom multiplexed protocol, AOF (Append-Only File) persistence, and high-speed memory eviction.

```java
client.executeAsync("SET", "db_name", "kvstore")
    .thenRun(() -> System.out.println("I am asynchronous!")); 
```

### Why kvstore?
* **Blazing fast:** Built on Netty's non-blocking event loop, achieving 90k+ OPS.
* **Multiplexed Protocol:** Support for multiple requests over a single TCP connection to
  reduce handshake overhead.

### Why not other systems?


Unlike solutions like Redis, which is single-threaded, kvstore utilizes and supports
multi-threaded operations out of the box to maximize CPU utilization across all cores, while maintaining
strict thread safety for its internal storage.