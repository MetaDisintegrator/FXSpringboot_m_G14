package org.fxtravel.services.user.dto;

import jakarta.annotation.Nullable;
import org.fxtravel.services.user.common.*;

public class UserDTO {
    @Nullable
    private int id;
    private String email;
    private String password;
    private boolean verified;
    private String username;
    private Gender gender;
    private Role role;
}
