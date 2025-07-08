# Simple Chat Application Using Redis

A real-time chat application built with Spring Boot, WebSocket, and Redis for scalable messaging and data persistence.

## Features

- **Real-time Messaging**: WebSocket-based instant messaging
- **Chat Rooms**: Create and join multiple chat rooms
- **Message History**: Persistent message storage with Redis
- **User Management**: Track active users in rooms
- **Redis Integration**: Pub/Sub for real-time updates and data storage
- **REST API**: RESTful endpoints for chat operations
- **Web Interface**: Simple HTML/CSS/JavaScript chat client

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Real-time Communication**: WebSocket with STOMP
- **Database**: Redis (data storage and pub/sub)
- **Frontend**: HTML5, CSS3, JavaScript
- **Build Tool**: Maven
- **Testing**: JUnit 5, Mockito

## Prerequisites

- Java 17 or higher
- Redis server (running on localhost:6379)
- Maven 3.6 or higher

## Setup Instructions

### 1. Redis Installation

**Windows:**
```bash
# Download Redis from official website or use Docker
docker run -d -p 6379:6379 redis:latest
```

**Linux/Mac:**
```bash
# Install Redis
sudo apt-get install redis-server  # Ubuntu/Debian
brew install redis                 # macOS

# Start Redis server
redis-server
```

### 2. Build and Run

```bash
# Clone or navigate to project directory
cd simple-chat-redis

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access the Application

- **Web Interface**: `http://localhost:8080/`
- **API Documentation**: Available through REST endpoints

## API Endpoints

### Chat Rooms

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat/rooms` | Create a new chat room |
| GET | `/api/chat/rooms` | Get all chat rooms |
| GET | `/api/chat/rooms/{roomId}` | Get specific room details |
| DELETE | `/api/chat/rooms/{roomId}` | Delete a chat room |

### Messages

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/rooms/{roomId}/messages` | Get message history |
| POST | `/api/chat/rooms/{roomId}/messages` | Send a message |

### Users

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/rooms/{roomId}/users` | Get room users |
| GET | `/api/chat/rooms/{roomId}/users/count` | Get user count |

### WebSocket Endpoints

| Destination | Description |
|-------------|-------------|
| `/app/chat.sendMessage/{roomId}` | Send message to room |
| `/app/chat.joinRoom/{roomId}` | Join a chat room |
| `/app/chat.leaveRoom/{roomId}` | Leave a chat room |
| `/topic/room/{roomId}` | Subscribe to room messages |

## Usage Examples

### 1. Create a Chat Room

```bash
curl -X POST http://localhost:8080/api/chat/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "name": "General Discussion",
    "description": "Main chat room for general discussions"
  }'
```

### 2. Send a Message

```bash
curl -X POST http://localhost:8080/api/chat/rooms/{roomId}/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hello everyone!",
    "sender": "john_doe"
  }'
```

### 3. Get Message History

```bash
curl http://localhost:8080/api/chat/rooms/{roomId}/messages
```

## WebSocket Client Example

```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to room messages
    stompClient.subscribe('/topic/room/general', function(message) {
        const chatMessage = JSON.parse(message.body);
        console.log('Received:', chatMessage);
    });
    
    // Send a message
    stompClient.send('/app/chat.sendMessage/general', {}, JSON.stringify({
        content: 'Hello World!',
        sender: 'username'
    }));
});
```

## Redis Data Structure

The application uses the following Redis data patterns:

- **Messages**: `room:{roomId}:messages` (List)
- **Room Info**: `room:{roomId}` (Hash)
- **Room Users**: `room:{roomId}:users` (Set)
- **Pub/Sub**: `chat:room:{roomId}` (Channel)

## Configuration

### Application Properties

```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.database=0

# Message History Limit
app.chat.message-history-limit=100
```

## Testing

Run the tests using Maven:

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/chat/
│   │       ├── SimpleChatApplication.java
│   │       ├── config/
│   │       │   ├── RedisConfig.java
│   │       │   └── WebSocketConfig.java
│   │       ├── controller/
│   │       │   ├── ChatController.java
│   │       │   ├── WebController.java
│   │       │   └── WebSocketController.java
│   │       ├── model/
│   │       │   ├── ChatMessage.java
│   │       │   └── ChatRoom.java
│   │       └── service/
│   │           ├── ChatService.java
│   │           └── RedisService.java
│   ├── resources/
│   │   ├── static/
│   │   │   ├── index.html
│   │   │   └── styles.css
│   │   └── application.properties
└── test/
    └── java/
        └── com/example/chat/
            └── ChatServiceTest.java
```

## Key Features Implemented

1. **✅ Real-time Messaging**: WebSocket with STOMP protocol
2. **✅ Chat Rooms**: Create, join, and manage multiple rooms
3. **✅ Message History**: Redis-based persistent message storage
4. **✅ User Management**: Track active users per room
5. **✅ Redis Integration**: Pub/Sub and data storage
6. **✅ REST API**: Complete RESTful API for chat operations
7. **✅ Web Interface**: Simple chat client
8. **✅ Testing**: Comprehensive unit tests

## Scaling Considerations

- **Redis Pub/Sub**: Enables horizontal scaling across multiple application instances
- **Message History**: Configurable limit to prevent memory issues
- **Connection Pool**: Optimized Redis connection management
- **WebSocket**: Efficient real-time communication

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is for educational purposes only.
