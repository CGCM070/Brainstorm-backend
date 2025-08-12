package org.brainstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdeaUpdateMessage {
    private String type; // "CREATE", "UPDATE", "DELETE", "VOTE"
    private Long ideaId;
    private String roomCode;
    private Object data; // La idea completa o datos específicos del cambio
    private String username; // Usuario que realizó la acción
}
