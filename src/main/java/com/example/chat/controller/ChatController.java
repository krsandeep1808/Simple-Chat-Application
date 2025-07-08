package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatRoom;
import com.example.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest request) {
        ChatRoom room = chatService.createRoom(request.getName(), request.getDescription());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/rooms")
    public ResponseEntity<Set<String>> getAllRooms() {
        Set<String> rooms = chatService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoom> getRoom(@PathVariable String roomId) {
        ChatRoom room = chatService.getRoom(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId) {
        chatService.deleteRoom(roomId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessageHistory(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getMessageHistory(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/rooms/{roomId}/users")
    public ResponseEntity<Set<String>> getRoomUsers(@PathVariable String roomId) {
        Set<String> users = chatService.getRoomUsers(roomId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/rooms/{roomId}/users/count")
    public ResponseEntity<Long> getRoomUserCount(@PathVariable String roomId) {
        Long count = chatService.getRoomUserCount(roomId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable String roomId,
                                                  @RequestBody ChatMessage message) {
        message.setRoomId(roomId);
        message.setType(ChatMessage.MessageType.CHAT);
        chatService.sendMessage(message);
        return ResponseEntity.ok(message);
    }

    // DTOs
    public static class CreateRoomRequest {
        private String name;
        private String description;

        public CreateRoomRequest() {}

        public CreateRoomRequest(String name, String description) {
            this.name = name;
            this.description = description;
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
    }
}
