package org.fxtravel.services.user.dto;

import lombok.Data;
import org.fxtravel.services.user.common.Gender;

@Data
public class UpdateUserDataRequest {
    private String username;
    private Gender gender;
}
