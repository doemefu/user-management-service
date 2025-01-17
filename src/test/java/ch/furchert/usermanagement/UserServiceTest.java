package ch.furchert.usermanagement;

import ch.furchert.usermanagement.dto.UserDto;
import ch.furchert.usermanagement.dto.request.CreateUserRequest;
import ch.furchert.usermanagement.entity.Role;
import ch.furchert.usermanagement.entity.Status;
import ch.furchert.usermanagement.entity.User;
import ch.furchert.usermanagement.exception.UserNotFoundException;
import ch.furchert.usermanagement.repository.UserRepository;
import ch.furchert.usermanagement.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TestSuite brought to you by ChatGPT hehe


class UserServiceTest {

    @Test
    void testCreateUser() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Input data
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        // Expected data
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .passwordHash("hashed_password")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        // Mock behavior
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Execute the service method
        User result = userService.createUser(request);

        // Assertions
        assertNotNull(result);
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("hashed_password", result.getPasswordHash());
        assertEquals(Role.USER, result.getRole());
        assertEquals(Status.ACTIVE, result.getStatus());

        // Verify interactions
        verify(userRepository, times(1)).existsByUsername("john");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Input data
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        // Mock behavior
        when(userRepository.existsByUsername("john")).thenReturn(true);

        // Execute and assert exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Username is already taken", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsByUsername("john");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Input data
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        // Mock behavior
        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // Execute and assert exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Email is already in use", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).existsByUsername("john");
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testGetUserById_UserExists() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, null);

        // Expected user
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Execute
        User result = userService.getUserById(1L);

        // Assertions
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, null);

        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and assert exception
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(1L));

        assertEquals("User with ID 1 not found", exception.getMessage());

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUser() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, null);

        // Existing user
        User existingUser = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role(Role.USER)
                .status(Status.ACTIVE)
                .build();

        // Updated data
        UserDto userDto = UserDto.builder()
                .username("john_updated")
                .email("john.updated@example.com")
                .role("ADMIN")
                .build();

        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation
                -> invocation.getArgument(0));

        // Execute
        User updatedUser = userService.updateUser(1L, userDto);

        // Assertions
        assertNotNull(updatedUser);
        assertEquals("john_updated", updatedUser.getUsername());
        assertEquals("john.updated@example.com", updatedUser.getEmail());
        assertEquals(Role.ADMIN, updatedUser.getRole());

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testResetPassword() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Existing user
        User existingUser = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .passwordHash("old_hashed_password")
                .build();

        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("new_password")).thenReturn("new_hashed_password");

        // Execute
        userService.resetPassword(1L, "new_password");

        // Assertions
        assertEquals("new_hashed_password", existingUser.getPasswordHash());

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("new_password");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testDeleteUser() {
        // Mock dependencies
        UserRepository userRepository = mock(UserRepository.class);

        // Initialize the service
        UserServiceImpl userService = new UserServiceImpl(userRepository, null);

        // Existing user
        User existingUser = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .build();

        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        doNothing().when(userRepository).delete(existingUser);

        // Execute
        userService.deleteUser(1L);

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(existingUser);
    }

}
