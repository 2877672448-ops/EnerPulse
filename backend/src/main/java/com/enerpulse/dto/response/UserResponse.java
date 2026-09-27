package com.enerpulse.dto.response;

import com.enerpulse.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserResponse extends BaseDTO {
    private Long tenantId;
    private String username;
    private String nickname;
    private String mobile;
    private String email;
    private String status;
    private OffsetDateTime lastLoginAt;
}
