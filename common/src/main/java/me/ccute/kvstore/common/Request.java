package me.ccute.kvstore.common;


/**
 * Represents a standard protocol request in kvstore.
 * <p>
 *     This object is passed between the SDK and the Server. It contains the necessary opcode,
 *     key, and payload to execute a command.
 * </p>
 * <b>Binary Protocol Structure:</b><br>
 * [1 byte opcode] [4 byte key length] [N byte key] [4 byte Value length] [N byte value]
 */
public class Request {

    public long requestId;

    public String command;
    public String[] args;

    public Request(long requestId, String cmd, String[] array) {
        this.requestId = requestId;
        this.command = cmd;
        this.args = array != null ? array : new String[0];
    }
}
