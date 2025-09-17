package org.brainstorm.config;

import org.brainstorm.service.WebSocketService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final WebSocketService webSocketService;
    public WebSocketEventListener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Nueva conexión WebSocket establecida");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String roomCode = (String) headerAccessor.getSessionAttributes().get("roomCode");
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (roomCode != null && username != null) {
            System.out.println("Usuario " + username + " desconectado de sala " + roomCode);
            webSocketService.userDisconnectedFromRoom(roomCode, username);
        }
    }
}
