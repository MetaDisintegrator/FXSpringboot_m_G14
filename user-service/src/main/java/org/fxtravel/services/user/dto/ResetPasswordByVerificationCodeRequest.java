package org.fxtravel.services.user.dto;

import lombok.Data;

@Data
public class ResetPasswordByVerificationCodeRequest {
    private String email;
    private String code;
    private String password;
}
