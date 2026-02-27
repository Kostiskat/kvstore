package me.ccute.kvstore.sdk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A builder for creating and configuring {@link KvClient} instances.
 * <p>
 *     This class provides a fluent API to configure the network settings before
 *     establishing a connection to the kvstore database.
 *     <p>
 *         <b>Example Usage:</b>
 *         <pre>{@code
 *         KvClient client = new KvClientBuilder()
 *                           .host("127.0.0.1")
 *                           .port(6379)
 *                           .timeout(5000)
 *                           .build();
 *         }</pre>
 *     </p>
 * </p>
 */
public class KvClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(KvClientBuilder.class);

    // Defaults
    private String host = "localhost";
    private int port = 6379;
    private int timeoutMs = 5000;

    /**
     * Sets the port of the kvstore server.
     *
     * @param host The hostname or IP address. Default is "localhost".
     * @return this builder instance for method chaining.
     */
    public KvClientBuilder host(String host) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("Host cannot be null or empty.");
        }
        this.host = host;
        return this;
    }

    /**
     * Sets the port of the kvstore server.
     *
     * @param port The port number. Default is 6379.
     * @return this builder instance for method chaining.
     */
    public KvClientBuilder port(int port) {
        if (port < 1 || port > 65535)  {
            throw new IllegalArgumentException("Port must be between 1 and 65535.");
        }
        this.port = port;
        return this;
    }

    public KvClientBuilder timeout(int timeoutMs) {
        if (timeoutMs <= 0) {
            throw  new IllegalArgumentException("Timeout must be greater than 0.");
        }
        this.timeoutMs = timeoutMs;
        return this;
    }

    public KvClient build() {
        log.info("Building KvClient connecting to {}:{} with timeout {}ms", host, port, timeoutMs);

        return new NettyKvClient(host, port, timeoutMs);
    }
}
