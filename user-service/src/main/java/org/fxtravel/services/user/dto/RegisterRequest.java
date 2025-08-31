package org.fxtravel.services.user.dto;

import lombok.Data;
import org.fxtravel.services.user.common.*;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private Gender gender;
    private Role role;
}
