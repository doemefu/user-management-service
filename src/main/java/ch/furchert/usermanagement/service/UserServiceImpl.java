package ch.furchert.usermanagement.service;

import ch.furchert.usermanagement.dto.UserDto;
import ch.furchert.usermanagement.dto.request.CreateUserRequest;
import ch.furchert.usermanagement.entity.Role;
import ch.furchert.usermanagement.entity.Status;
import ch.furchert.usermanagement.entity.User;
import ch.furchert.usermanagement.exception.UserNotFoundException;
import ch.furchert.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // If you're using Spring Security for hashing

    @Override
    public User createUser(CreateUserRequest userRequest) {
        // Check for existing username
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        // Check for existing email
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .role(Role.USER)
                .status(Status.ACTIVE)
                .lastLogin(null)
                .passwordHash(passwordEncoder.encode(userRequest.getPassword()))
                .build();
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, UserDto userDto) {
        User existingUser = getUserById(id);
        existingUser.setUsername(userDto.getUsername());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setRole(Role.valueOf(userDto.getRole()));
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        User user = getUserById(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
