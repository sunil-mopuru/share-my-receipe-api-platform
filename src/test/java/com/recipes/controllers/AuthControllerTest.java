package com.recipes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipes.entities.Chef;
import com.recipes.payload.request.SignupRequest;
import com.recipes.repositories.ChefRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChefRepository chefRepository;

    @Test
    public void testSignUpSuccess() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setHandle("testchef");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        when(chefRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(chefRepository.existsByHandle("testchef")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Chef registered successfully!"));
    }

    @Test
    public void testSignUpEmailAlreadyExists() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setHandle("testchef");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        when(chefRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    public void testSignUpHandleAlreadyExists() throws Exception {
        // Given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setHandle("testchef");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");

        when(chefRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(chefRepository.existsByHandle("testchef")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Handle is already taken!"));
    }
}