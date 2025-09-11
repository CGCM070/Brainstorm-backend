package org.brainstorm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.brainstorm.model.Users;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Users user;
    private String token;
}
