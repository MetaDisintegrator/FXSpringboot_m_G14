package org.fxtravel.services.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private Integer userId;
}
