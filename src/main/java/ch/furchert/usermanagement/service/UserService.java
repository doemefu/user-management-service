package ch.furchert.usermanagement.service;

import ch.furchert.usermanagement.dto.UserDto;
import ch.furchert.usermanagement.dto.request.CreateUserRequest;
import ch.furchert.usermanagement.entity.User;

import java.util.List;

public interface UserService {
    User createUser(CreateUserRequest userRequest);
    User getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    void resetPassword(Long id, String newPassword);
}
