package ch.furchert.usermanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String role; // Use enums for internal logic, if needed
    private String status; // Optional: Map enums to strings for flexibility
    private Date created;
    private Date updated;
    private Date lastLogin;
}
