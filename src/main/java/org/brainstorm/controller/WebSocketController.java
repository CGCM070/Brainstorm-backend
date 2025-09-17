package org.brainstorm.controller;

import org.brainstorm.service.WebSocketService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

@Controller
public class WebSocketController {

    private final WebSocketService webSocketService;

    public WebSocketController(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @MessageMapping("/room/{roomCode}/connect")
    public void userConnect(@DestinationVariable String roomCode,
                           @Payload String username,
                           StompHeaderAccessor headerAccessor) {
        // Almacenar información en la sesión para usar en disconnect
        headerAccessor.getSessionAttributes().put("roomCode", roomCode);
        headerAccessor.getSessionAttributes().put("username", username);

        webSocketService.userConnectedToRoom(roomCode, username);
    }

    @MessageMapping("/room/{roomCode}/disconnect")
    public void userDisconnect(@DestinationVariable String roomCode,
                              @Payload String username) {
        webSocketService.userDisconnectedFromRoom(roomCode, username);
    }

    @SubscribeMapping("/room/{roomCode}/users")
    public void subscribeToUsers(@DestinationVariable String roomCode) {
        // Este método se ejecuta cuando alguien se suscribe al canal de usuarios
        // Aquí podrías enviar el estado actual de usuarios conectados
        System.out.println("Usuario suscrito a actualizaciones de usuarios en sala: " + roomCode);
    }
}
