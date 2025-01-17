package ch.furchert.usermanagement;

import ch.furchert.usermanagement.controller.UserController;
import ch.furchert.usermanagement.dto.UserDto;
import ch.furchert.usermanagement.dto.request.CreateUserRequest;
import ch.furchert.usermanagement.entity.User;
import ch.furchert.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService; // This will use the manually defined mock

    @Autowired
    private ObjectMapper objectMapper;

    @Configuration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return mock(UserService.class);
        }
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(userService); // Reset the mock for a clean state before each test
    }

    @Test
    void testCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");
        request.setPassword("password123");

        User createdUser = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role(ch.furchert.usermanagement.entity.Role.USER)
                .status(ch.furchert.usermanagement.entity.Status.ACTIVE)
                .created(new Date())
                .updated(new Date())
                .lastLogin(null)
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    void testGetUserById() throws Exception {
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role(ch.furchert.usermanagement.entity.Role.USER)
                .status(ch.furchert.usermanagement.entity.Status.ACTIVE)
                .created(new Date())
                .updated(new Date())
                .lastLogin(null)
                .build();

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .role(ch.furchert.usermanagement.entity.Role.USER)
                .status(ch.furchert.usermanagement.entity.Status.ACTIVE)
                .build();

        User user2 = User.builder()
                .id(2L)
                .username("jane")
                .email("jane@example.com")
                .role(ch.furchert.usermanagement.entity.Role.USER)
                .status(ch.furchert.usermanagement.entity.Status.ACTIVE)
                .build();

        List<User> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("john"))
                .andExpect(jsonPath("$[1].username").value("jane"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void testUpdateUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .username("john_updated")
                .email("john.updated@example.com")
                .role("ADMIN")
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("john_updated")
                .email("john.updated@example.com")
                .role(ch.furchert.usermanagement.entity.Role.ADMIN)
                .status(ch.furchert.usermanagement.entity.Status.ACTIVE)
                .build();

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_updated"))
                .andExpect(jsonPath("$.email").value("john.updated@example.com"));

        verify(userService, times(1)).updateUser(eq(1L), any(UserDto.class));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void testResetPassword() throws Exception {
        doNothing().when(userService).resetPassword(eq(1L), eq("new_password"));

        mockMvc.perform(post("/users/1/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"new_password\""))
                .andExpect(status().isOk());

        verify(userService, times(1)).resetPassword(eq(1L), eq("new_password"));
    }
}
