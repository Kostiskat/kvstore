package me.ccute.kvstore.sdk.client;

import me.ccute.kvstore.sdk.exceptions.KvException;

import java.util.concurrent.CompletableFuture;

/**
 * The primary client for interacting with the kvstore database.
 * <p>
 *     Implementations of this interface are completely thread-safe and are designed
 *     to be used across multiple threads in high-throughput applications.
 *     Users should instantiate this via {@link KvClientBuilder}.
 * </p>
 */
public interface KvClient extends AutoCloseable {
    /**
     * Stores a value in the database associated with the given key.
     *
     * @param key The unique identifier for the data. Cannot be null or empty.
     * @param value The string value to store. Cannot be null.
     * @throws IllegalArgumentException if the key or value is invalid.
     * @throws KvException if a network or server error occurs.
     */
    void set(String key, String value);

    /**
     * Retrieves the value associated with the given key.
     *
     * @param key The unique identifier for the data. Cannot be null or empty.
     * @return The value, or null if the key does not exist.
     * @throws KvException if a network or server error occurs.
     */
    String get(String key);

    /**
     * Deletes the given key and its associated value from the database.
     *
     * @param key The unique identifier for the data. Cannot be null or empty.
     * @return true if the key was deleted, false if it did not exist.
     * @throws KvException if a network or server error occurs.
     */
    boolean delete(String key);


    /**
     * Increments the numeric value stored at the specified key by the given amount.
     * <p>
     * If the key does not exist, it is initialized to 0 before performing the increment.
     * If the stored value cannot be parsed as an integer, a {@link KvException} is thrown.
     *
     * @param key       The unique identifier for the data. Cannot be null or empty.
     * @param increment The amount to add to the current value. Can be negative to decrement.
     * @throws IllegalArgumentException if the key is invalid.
     * @throws KvException if a network error occurs or the existing value is not a number.
     */
    void increment(String key, int increment);

    /**
     * Increments the numeric value stored at the specified key by exactly 1.
     * <p>
     * This is a convenience method equivalent to calling
     * {@link #increment(String, int) increment(key, 1)}.
     *
     * @param key The unique identifier for the data. Cannot be null or empty.
     * @see #increment(String, int)
     */
    void increment(String key);

    /**
     * Executes a raw command against the server.
     * Useful for CLI tools or invoking custom server commands not yet typed in the SDK.
     *
     * @param command The command to execute (e.g., "SET").
     * @param args    The arguments for the command.
     * @return The string response from the server.
     * @throws KvException if the command fails or times out.
     */
    String execute(String command, String... args);

    /**
     * Executes a command asynchronously, returning a Future.
     * Perfect for high-throughput pipelining.
     */
    java.util.concurrent.CompletableFuture<String> executeAsync(String command, String... args);
}