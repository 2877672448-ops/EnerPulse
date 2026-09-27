package com.enerpulse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String email;
    private String status;
}
