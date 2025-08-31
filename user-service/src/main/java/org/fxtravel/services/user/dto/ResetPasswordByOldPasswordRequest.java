package org.fxtravel.services.user.dto;

import lombok.Data;

@Data
public class ResetPasswordByOldPasswordRequest {
    String email;
    String oldPassword;
    String newPassword;
}
