package com.example.chat.controller;

import com.example.chat.model.ChatMessage;
import com.example.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    public WebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.sendMessage/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId,
                                  @Payload ChatMessage chatMessage) {
        chatMessage.setRoomId(roomId);
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatService.sendMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.joinRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage joinRoom(@DestinationVariable String roomId,
                               @Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        
        // Store username in WebSocket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("roomId", roomId);
        
        chatMessage.setRoomId(roomId);
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatService.joinRoom(roomId, chatMessage.getSender());
        
        return chatMessage;
    }

    @MessageMapping("/chat.leaveRoom/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public ChatMessage leaveRoom(@DestinationVariable String roomId,
                                @Payload ChatMessage chatMessage) {
        chatMessage.setRoomId(roomId);
        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatService.leaveRoom(roomId, chatMessage.getSender());
        return chatMessage;
    }
}
