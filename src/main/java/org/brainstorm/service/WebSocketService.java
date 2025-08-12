package org.brainstorm.service;

import org.brainstorm.dto.IdeaUpdateMessage;
import org.brainstorm.dto.IdeaMapper;
import org.brainstorm.dto.IdeaWebSocketDto;
import org.brainstorm.model.Ideas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Map para rastrear usuarios conectados por sala
    // roomCode -> Set<username>
    private final ConcurrentHashMap<String, Set<String>> roomConnections = new ConcurrentHashMap<>();

    public void notifyIdeaCreated(String roomCode, Ideas idea, String username) {
        IdeaWebSocketDto ideaDto = IdeaMapper.toWebSocketDto(idea);
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "CREATE",
            null,
            roomCode,
            ideaDto,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    public void notifyIdeaUpdated(String roomCode, Long ideaId, Ideas idea, String username) {
        IdeaWebSocketDto ideaDto = IdeaMapper.toWebSocketDto(idea);
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "UPDATE",
            ideaId,
            roomCode,
            ideaDto,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    public void notifyIdeaDeleted(String roomCode, Long ideaId, String username) {
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "DELETE",
            ideaId,
            roomCode,
            null,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    public void notifyIdeaVoted(String roomCode, Long ideaId, Ideas updatedIdea, String username) {
        IdeaWebSocketDto ideaDto = IdeaMapper.toWebSocketDto(updatedIdea);
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "VOTE",
            ideaId,
            roomCode,
            ideaDto,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    // Métodos para comentarios
    public void notifyCommentCreated(String roomCode, Long ideaId, Object comment, String username) {
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "COMMENT_CREATE",
            ideaId,
            roomCode,
            comment,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    public void notifyCommentUpdated(String roomCode, Long ideaId, Object comment, String username) {
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "COMMENT_UPDATE",
            ideaId,
            roomCode,
            comment,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    public void notifyCommentDeleted(String roomCode, Long ideaId, Long commentId, String username) {
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "COMMENT_DELETE",
            ideaId,
            roomCode,
            commentId,
            username
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/ideas", message);
    }

    // Métodos para manejar usuarios conectados
    public void userConnectedToRoom(String roomCode, String username) {
        roomConnections.computeIfAbsent(roomCode, k -> new CopyOnWriteArraySet<>()).add(username);
        notifyUserCountUpdate(roomCode);
        System.out.println("Usuario " + username + " conectado a sala " + roomCode +
                          ". Total usuarios: " + getConnectedUsersCount(roomCode));
    }

    public void userDisconnectedFromRoom(String roomCode, String username) {
        Set<String> users = roomConnections.get(roomCode);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                roomConnections.remove(roomCode);
            }
            notifyUserCountUpdate(roomCode);
            System.out.println("Usuario " + username + " desconectado de sala " + roomCode +
                              ". Total usuarios: " + getConnectedUsersCount(roomCode));
        }
    }

    public int getConnectedUsersCount(String roomCode) {
        Set<String> users = roomConnections.get(roomCode);
        return users != null ? users.size() : 0;
    }

    public Set<String> getConnectedUsers(String roomCode) {
        return roomConnections.getOrDefault(roomCode, new CopyOnWriteArraySet<>());
    }

    private void notifyUserCountUpdate(String roomCode) {
        int userCount = getConnectedUsersCount(roomCode);
        Set<String> connectedUsers = getConnectedUsers(roomCode);

        // Crear mensaje con información de usuarios conectados
        IdeaUpdateMessage message = new IdeaUpdateMessage(
            "USER_COUNT_UPDATE",
            null,
            roomCode,
            new UserCountData(userCount, connectedUsers),
            "SYSTEM"
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomCode + "/users", message);
    }

    // Clase interna para los datos de conteo de usuarios
    public static class UserCountData {
        private int count;
        private Set<String> users;

        public UserCountData(int count, Set<String> users) {
            this.count = count;
            this.users = users;
        }

        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public Set<String> getUsers() { return users; }
        public void setUsers(Set<String> users) { this.users = users; }
    }
}
