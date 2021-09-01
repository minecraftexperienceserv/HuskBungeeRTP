package me.william278.huskbungeertp.jedis;

public record RedisMessage(String targetServer,
                           RedisMessageType messageType,
                           String messageData) {

    public String toString() {
        return targetServer + "£" + messageType.toString() + "£" + messageData;
    }

    public String getTargetServer() {
        return targetServer;
    }

    public RedisMessageType getMessageType() {
        return messageType;
    }

    public String getMessageData() {
        return messageData;
    }

    public enum RedisMessageType {
        REQUEST_RANDOM_LOCATION,
        REPLY_RANDOM_LOCATION
    }
}
