package com.example.chat.model;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

public class ChatRoom {
    
    private String id;
    private String name;
    private String description;
    private Set<String> activeUsers;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    
    // Constructors
    public ChatRoom() {
        this.activeUsers = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    public ChatRoom(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.activeUsers = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Set<String> getActiveUsers() {
        return activeUsers;
    }
    
    public void setActiveUsers(Set<String> activeUsers) {
        this.activeUsers = activeUsers;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    // Utility methods
    public void addUser(String username) {
        this.activeUsers.add(username);
        this.lastActivity = LocalDateTime.now();
    }
    
    public void removeUser(String username) {
        this.activeUsers.remove(username);
        this.lastActivity = LocalDateTime.now();
    }
    
    public int getUserCount() {
        return activeUsers.size();
    }
    
    @Override
    public String toString() {
        return "ChatRoom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", activeUsers=" + activeUsers +
                ", createdAt=" + createdAt +
                ", lastActivity=" + lastActivity +
                '}';
    }
}
