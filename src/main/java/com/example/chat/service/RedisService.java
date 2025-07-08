package com.example.chat.service;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatRoom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessageListenerContainer messageListenerContainer;
    private final ObjectMapper objectMapper;

    @Value("${app.chat.message-history-limit:100}")
    private int messageHistoryLimit;

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate, 
                       RedisMessageListenerContainer messageListenerContainer,
                       ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.messageListenerContainer = messageListenerContainer;
        this.objectMapper = objectMapper;
    }

    // Message operations
    public void saveMessage(ChatMessage message) {
        message.setId(UUID.randomUUID().toString());
        String key = "room:" + message.getRoomId() + ":messages";
        
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(key, messageJson);
            
            // Trim list to keep only recent messages
            redisTemplate.opsForList().trim(key, -messageHistoryLimit, -1);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error saving message to Redis", e);
        }
    }

    public List<ChatMessage> getMessageHistory(String roomId) {
        String key = "room:" + roomId + ":messages";
        List<Object> messages = redisTemplate.opsForList().range(key, 0, -1);
        
        return messages.stream()
                .map(msg -> {
                    try {
                        return objectMapper.readValue(msg.toString(), ChatMessage.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error parsing message from Redis", e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Room operations
    public void createRoom(ChatRoom room) {
        String key = "room:" + room.getId();
        redisTemplate.opsForHash().putAll(key, objectToMap(room));
    }

    public ChatRoom getRoom(String roomId) {
        String key = "room:" + roomId;
        if (!redisTemplate.hasKey(key)) {
            return null;
        }
        
        try {
            Object roomData = redisTemplate.opsForHash().entries(key);
            String roomJson = objectMapper.writeValueAsString(roomData);
            return objectMapper.readValue(roomJson, ChatRoom.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error getting room from Redis", e);
        }
    }

    public Set<String> getAllRooms() {
        return redisTemplate.keys("room:*")
                .stream()
                .filter(key -> !key.contains(":messages"))
                .map(key -> key.substring(5)) // Remove "room:" prefix
                .collect(Collectors.toSet());
    }

    public void deleteRoom(String roomId) {
        String roomKey = "room:" + roomId;
        String messagesKey = "room:" + roomId + ":messages";
        
        redisTemplate.delete(roomKey);
        redisTemplate.delete(messagesKey);
    }

    // User operations
    public void addUserToRoom(String roomId, String username) {
        String key = "room:" + roomId + ":users";
        redisTemplate.opsForSet().add(key, username);
    }

    public void removeUserFromRoom(String roomId, String username) {
        String key = "room:" + roomId + ":users";
        redisTemplate.opsForSet().remove(key, username);
    }

    public Set<String> getRoomUsers(String roomId) {
        String key = "room:" + roomId + ":users";
        return redisTemplate.opsForSet().members(key)
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    public Long getRoomUserCount(String roomId) {
        String key = "room:" + roomId + ":users";
        return redisTemplate.opsForSet().size(key);
    }

    // Publish message to Redis pub/sub
    public void publishMessage(String channel, ChatMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(channel, messageJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error publishing message to Redis", e);
        }
    }

    // Utility methods
    private java.util.Map<String, Object> objectToMap(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return objectMapper.readValue(json, java.util.Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to map", e);
        }
    }
}
