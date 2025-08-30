package dto;

import lombok.Data;
import org.fxtravel.fxspringboot.common.Gender;

@Data
public class UpdateUserDataRequest {
    private String username;
    private Gender gender;
}
