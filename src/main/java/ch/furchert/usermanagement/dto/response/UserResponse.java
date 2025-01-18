package ch.furchert.usermanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserResponse {
    private String username;
    private String email;
    private String role;
    private String userStatus;
    private Date created;
    private Date updated;
    private Date lastLogin;
}
