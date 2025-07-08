package com.example.chat.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class ChatMessage {
    
    private String id;
    private String content;
    private String sender;
    private String roomId;
    private MessageType type;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        SYSTEM
    }
    
    // Constructors
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ChatMessage(String content, String sender, String roomId, MessageType type) {
        this.content = content;
        this.sender = sender;
        this.roomId = roomId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", roomId='" + roomId + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}
