package com.example.chat;

import com.example.chat.model.ChatMessage;
import com.example.chat.model.ChatRoom;
import com.example.chat.service.ChatService;
import com.example.chat.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private ChatService chatService;

    private ChatRoom testRoom;
    private ChatMessage testMessage;

    @BeforeEach
    void setUp() {
        testRoom = new ChatRoom("test-room-1", "Test Room", "A test chat room");
        testMessage = new ChatMessage("Hello World", "testuser", "test-room-1", ChatMessage.MessageType.CHAT);
    }

    @Test
    void testCreateRoom() {
        // Given
        when(redisService.createRoom(any(ChatRoom.class))).thenReturn(testRoom);

        // When
        ChatRoom result = chatService.createRoom("Test Room", "A test chat room");

        // Then
        assertNotNull(result);
        assertEquals("Test Room", result.getName());
        assertEquals("A test chat room", result.getDescription());
        verify(redisService, times(1)).createRoom(any(ChatRoom.class));
    }

    @Test
    void testGetRoom() {
        // Given
        when(redisService.getRoom("test-room-1")).thenReturn(testRoom);

        // When
        ChatRoom result = chatService.getRoom("test-room-1");

        // Then
        assertNotNull(result);
        assertEquals("test-room-1", result.getId());
        assertEquals("Test Room", result.getName());
        verify(redisService, times(1)).getRoom("test-room-1");
    }

    @Test
    void testGetAllRooms() {
        // Given
        Set<String> rooms = new HashSet<>(Arrays.asList("room1", "room2", "room3"));
        when(redisService.getAllRooms()).thenReturn(rooms);

        // When
        Set<String> result = chatService.getAllRooms();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("room1"));
        assertTrue(result.contains("room2"));
        assertTrue(result.contains("room3"));
        verify(redisService, times(1)).getAllRooms();
    }

    @Test
    void testSendMessage() {
        // Given
        doNothing().when(redisService).saveMessage(any(ChatMessage.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(ChatMessage.class));
        doNothing().when(redisService).publishMessage(anyString(), any(ChatMessage.class));

        // When
        chatService.sendMessage(testMessage);

        // Then
        verify(redisService, times(1)).saveMessage(testMessage);
        verify(messagingTemplate, times(1)).convertAndSend("/topic/room/test-room-1", testMessage);
        verify(redisService, times(1)).publishMessage("chat:room:test-room-1", testMessage);
    }

    @Test
    void testGetMessageHistory() {
        // Given
        List<ChatMessage> messages = Arrays.asList(
            new ChatMessage("Hello", "user1", "test-room-1", ChatMessage.MessageType.CHAT),
            new ChatMessage("Hi there", "user2", "test-room-1", ChatMessage.MessageType.CHAT)
        );
        when(redisService.getMessageHistory("test-room-1")).thenReturn(messages);

        // When
        List<ChatMessage> result = chatService.getMessageHistory("test-room-1");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals("Hi there", result.get(1).getContent());
        verify(redisService, times(1)).getMessageHistory("test-room-1");
    }

    @Test
    void testJoinRoom() {
        // Given
        doNothing().when(redisService).addUserToRoom("test-room-1", "testuser");
        doNothing().when(redisService).saveMessage(any(ChatMessage.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(ChatMessage.class));
        doNothing().when(redisService).publishMessage(anyString(), any(ChatMessage.class));

        // When
        chatService.joinRoom("test-room-1", "testuser");

        // Then
        verify(redisService, times(1)).addUserToRoom("test-room-1", "testuser");
        verify(redisService, times(1)).saveMessage(any(ChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/room/test-room-1"), any(ChatMessage.class));
    }

    @Test
    void testLeaveRoom() {
        // Given
        doNothing().when(redisService).removeUserFromRoom("test-room-1", "testuser");
        doNothing().when(redisService).saveMessage(any(ChatMessage.class));
        doNothing().when(messagingTemplate).convertAndSend(anyString(), any(ChatMessage.class));
        doNothing().when(redisService).publishMessage(anyString(), any(ChatMessage.class));

        // When
        chatService.leaveRoom("test-room-1", "testuser");

        // Then
        verify(redisService, times(1)).removeUserFromRoom("test-room-1", "testuser");
        verify(redisService, times(1)).saveMessage(any(ChatMessage.class));
        verify(messagingTemplate, times(1)).convertAndSend(eq("/topic/room/test-room-1"), any(ChatMessage.class));
    }

    @Test
    void testGetRoomUsers() {
        // Given
        Set<String> users = new HashSet<>(Arrays.asList("user1", "user2", "user3"));
        when(redisService.getRoomUsers("test-room-1")).thenReturn(users);

        // When
        Set<String> result = chatService.getRoomUsers("test-room-1");

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("user1"));
        assertTrue(result.contains("user2"));
        assertTrue(result.contains("user3"));
        verify(redisService, times(1)).getRoomUsers("test-room-1");
    }

    @Test
    void testGetRoomUserCount() {
        // Given
        when(redisService.getRoomUserCount("test-room-1")).thenReturn(5L);

        // When
        Long result = chatService.getRoomUserCount("test-room-1");

        // Then
        assertNotNull(result);
        assertEquals(5L, result);
        verify(redisService, times(1)).getRoomUserCount("test-room-1");
    }
}
