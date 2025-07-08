package com.example.chat.service;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatService {

    private final RedisService redisService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatService(RedisService redisService, SimpMessagingTemplate messagingTemplate) {
        this.redisService = redisService;
        this.messagingTemplate = messagingTemplate;
    }

    public ChatRoom createRoom(String name, String description) {
        String roomId = UUID.randomUUID().toString();
        ChatRoom room = new ChatRoom(roomId, name, description);
        redisService.createRoom(room);
        return room;
    }

    public ChatRoom getRoom(String roomId) {
        return redisService.getRoom(roomId);
    }

    public Set<String> getAllRooms() {
        return redisService.getAllRooms();
    }

    public void deleteRoom(String roomId) {
        redisService.deleteRoom(roomId);
    }

    public void sendMessage(ChatMessage message) {
        // Save message to Redis
        redisService.saveMessage(message);
        
        // Broadcast message to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoomId(), message);
        
        // Also publish to Redis pub/sub for scaling across multiple instances
        redisService.publishMessage("chat:room:" + message.getRoomId(), message);
    }

    public List<ChatMessage> getMessageHistory(String roomId) {
        return redisService.getMessageHistory(roomId);
    }

    public void joinRoom(String roomId, String username) {
        redisService.addUserToRoom(roomId, username);
        
        // Send join notification
        ChatMessage joinMessage = new ChatMessage(
            username + " joined the room",
            "System",
            roomId,
            ChatMessage.MessageType.JOIN
        );
        
        sendMessage(joinMessage);
    }

    public void leaveRoom(String roomId, String username) {
        redisService.removeUserFromRoom(roomId, username);
        
        // Send leave notification
        ChatMessage leaveMessage = new ChatMessage(
            username + " left the room",
            "System",
            roomId,
            ChatMessage.MessageType.LEAVE
        );
        
        sendMessage(leaveMessage);
    }

    public Set<String> getRoomUsers(String roomId) {
        return redisService.getRoomUsers(roomId);
    }

    public Long getRoomUserCount(String roomId) {
        return redisService.getRoomUserCount(roomId);
    }
}
