package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void find_user_by_id() throws Exception {
        User u = new User();
        long id = 1L;
        u.setId(id);
        u.setUsername("u");
        u.setPassword("pw");

        when(userRepo.findById(id)).thenReturn(Optional.of(u));

        final ResponseEntity<User> response = userController.findById(id);

        User c = response.getBody();
        assertNotNull(c);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("u", c.getUsername());
        assertEquals("pw", c.getPassword());
    }

    @Test
    public void find_user_by_name() throws Exception {
        User u = new User();
        long id = 1L;
        u.setId(id);
        u.setUsername("u");
        u.setPassword("pw");
        String user = "u";

        when(userRepo.findByUsername(user)).thenReturn(u);

        final ResponseEntity<User> response = userController.findByUserName(user);

        User c = response.getBody();
        assertNotNull(c);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("u", c.getUsername());
        assertEquals("pw", c.getPassword());
    }

    @Test
    public void user_id_not_found() throws Exception {
        final ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void user_name_not_found() throws Exception {
        final ResponseEntity<User> response = userController.findByUserName("negUser");
        assertEquals(404, response.getStatusCodeValue());
    }
}
