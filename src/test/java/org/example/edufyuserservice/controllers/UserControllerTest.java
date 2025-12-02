package org.example.edufyuserservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edufyuserservice.DTOs.RatingDTO;
import org.example.edufyuserservice.DTOs.UserDTO;
import org.example.edufyuserservice.clients.RatingsClient;
import org.example.edufyuserservice.configs.SecurityConfig;
import org.example.edufyuserservice.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServiceImpl userService;

    @MockitoBean
    private RatingsClient ratingsClient;

    @Test
    @WithMockUser(roles = "edufy_USER")
    void testTestEndpoint() throws Exception {
        mockMvc.perform(get("/users/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("This is the user controller test"));
    }

    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testRatings() throws Exception {
        when(ratingsClient.callRatingsService()).thenReturn("ratings OK");

        mockMvc.perform(get("/users/test-ratings"))
                .andExpect(status().isOk())
                .andExpect(content().string("ratings OK"));
    }


    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testUserLikes() throws Exception {
        List<RatingDTO> ratings = Arrays.asList(
                new RatingDTO(1L,"LIKE", "VIDEO", 4L),
                new RatingDTO(1L, "DISLIKE", "MUSIC", 10L)
        );

        when(ratingsClient.getUserLikes(1L)).thenReturn(ratings);

        mockMvc.perform(get("/users/userlikes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("LIKE"));
    }

    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testAddUser() throws Exception {
        UserDTO dto = new UserDTO(null, 1L, "testuser", "Test", "User", "t@mail.com", null, "edufy_USER", true);

        when(userService.newUser(any())).thenReturn(dto);

        mockMvc.perform(post("/users/newuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }


    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testGetAllUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(
                new UserDTO(null, 1L, "one", "One", "User", "1@mail.com", null, "edufy_USER", true),
                new UserDTO(null, 2L, "two", "Two", "User", "2@mail.com", null, "edufy_USER", true)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users/getall"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testGetUserById() throws Exception {
        UserDTO user = new UserDTO(null, 5L, "alex", "Alex", "Smith", "alex@mail.com", null, "edufy_USER", true);

        when(userService.getUserById(5L)).thenReturn(user);

        mockMvc.perform(get("/users/get/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alex"));
    }

    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testUpdateUser() throws Exception {
        UserDTO updated = new UserDTO(null, 7L, "updated", "Up", "Dated", "up@mail.com", null, "edufy_USER", true);

        when(userService.updateUser(any())).thenReturn(updated);

        objectMapper= new ObjectMapper();
        mockMvc.perform(put("/users/updateuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }


    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testInactivateUser() throws Exception {
        UserDTO inactive = new UserDTO(null, 9L, "inactiveUser", "In", "Active", "in@mail.com", null, "edufy_USER", false);

        when(userService.getUserById(9L)).thenReturn(inactive);

        mockMvc.perform(put("/users/inactivateuser/9"))
                .andExpect(status().isOk())
                .andExpect(content().string("User: inactiveUser set to inactive"));

        verify(userService).setUserInactive(9L);
    }

    @Test
    @WithMockUser(roles = {"edufy_ADMIN"})
    void testDeleteUser_AdminAllowed() throws Exception {
        mockMvc.perform(delete("/users/deleteuser/11"))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id: 11 deleted"));

        verify(userService).deleteUser(11L);
    }

    @Test
    @WithMockUser(roles = {"edufy_USER"})
    void testDeleteUser_ForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(delete("/users/deleteuser/11"))
                .andExpect(status().isForbidden());
    }
}

