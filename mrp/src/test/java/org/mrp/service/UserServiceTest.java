package org.mrp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mrp.exception.*;
import org.mrp.model.User;
import org.mrp.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setUsername("testuser");
    }


    @Test
    void registerUser_success() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.insertUser(eq("testuser"), anyString())).thenReturn(userId);

        UUID result = userService.registerUser("testuser", "password");

        assertEquals(userId, result);
    }

    @Test
    void registerUser_existingUsername_throwsUserAlreadyExists() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser("testuser", "password"));
    }

    @Test
    void registerUser_repositoryFails_throwsInternalServerException() throws Exception {
        when(userRepository.findByUsername("testuser")).thenThrow(RuntimeException.class);

        assertThrows(InternalServerException.class, () -> userService.registerUser("testuser", "password"));
    }


    @Test
    void loginUser_success_returnsToken() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(userRepository.getPasswordHash("testuser")).thenReturn(Optional.of(hash("password")));

        String token = userService.loginUser("testuser", "password");

        assertNotNull(token);
        assertTrue(token.contains("testuser"));
    }

    @Test
    void loginUser_usernameNotFound_throwsInvalidCredentials() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser("testuser", "password"));
    }

    @Test
    void loginUser_wrongPassword_throwsInvalidCredentials() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(userRepository.getPasswordHash("testuser")).thenReturn(Optional.of(hash("otherpassword")));

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser("testuser", "password"));
    }

    @Test
    void loginUser_repositoryFails_throwsInternalServerException() throws Exception {
        when(userRepository.findByUsername("testuser")).thenThrow(RuntimeException.class);

        assertThrows(InternalServerException.class, () -> userService.loginUser("testuser", "password"));
    }

    // ---------- TOKEN ----------

    @Test
    void getUserByToken_validToken_returnsUser() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        when(userRepository.getPasswordHash("testuser")).thenReturn(Optional.of(hash("password")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        String token = userService.loginUser("testuser", "password");
        User result = userService.getUserByToken(token);

        assertEquals(user, result);
    }

    @Test
    void getUserByToken_invalidToken_returnsNull() throws ApiException {
        User result = userService.getUserByToken("invalid-token");
        assertNull(result);
    }


    @Test
    void getUserByUsername_success() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("testuser");

        assertEquals(user, result);
    }

    @Test
    void getUserByUsername_notFound_throwsUserNotFound() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername("testuser"));
    }


    @Test
    void updateUserProfile_success() throws Exception {
        when(userRepository.updateProfile(userId, "mail@test.com", "sci-fi")).thenReturn(true);

        boolean updated = userService.updateUserProfile(userId, "mail@test.com", "sci-fi");

        assertTrue(updated);
    }

    @Test
    void updateUserProfile_repositoryFails_throwsInternalServerException() throws Exception {
        when(userRepository.updateProfile(any(), any(), any())).thenThrow(RuntimeException.class);

        assertThrows(InternalServerException.class, () -> userService.updateUserProfile(userId, "mail@test.com", "sci-fi"));
    }

    private String hash(String password) throws Exception {
        var md = java.security.MessageDigest.getInstance("SHA-256");
        return java.util.Base64.getEncoder()
                .encodeToString(md.digest(password.getBytes()));
    }
}
