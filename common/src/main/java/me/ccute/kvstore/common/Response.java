package me.ccute.kvstore.common;

public record Response(long requestId, byte status, byte[] payload) {
    public Response(long requestId,byte status, byte[] payload) {
        this.requestId = requestId;
        this.status = status;
        this.payload = payload != null ? payload : new byte[0];
    }

    public long getRequestId() {
        return requestId;
    }

    public byte getStatus() {
        return status;
    }

    public byte[] getPayload() {
        return payload;
    }
}
