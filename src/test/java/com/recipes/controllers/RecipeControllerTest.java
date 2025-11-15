package com.recipes.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipes.entities.Chef;
import com.recipes.entities.Recipe;
import com.recipes.payload.request.RecipeRequest;
import com.recipes.repositories.ChefRepository;
import com.recipes.repositories.RecipeRepository;
import com.recipes.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RecipeRepository recipeRepository;

    @MockBean
    private ChefRepository chefRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    public void testCreateRecipeUnauthorized() throws Exception {
        // Given
        RecipeRequest recipeRequest = new RecipeRequest();
        recipeRequest.setTitle("Test Recipe");
        recipeRequest.setIngredients(List.of("ingredient1", "ingredient2"));
        recipeRequest.setSteps(List.of("step1", "step2"));

        // When & Then
        mockMvc.perform(post("/api/recipes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(recipeRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetPublicRecipes() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/public/recipes")
                .param("page", "0")
                .param("page_size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPublicRecipesWithInvalidPageSize() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/public/recipes")
                .param("page", "0")
                .param("page_size", "100")) // Too large, should be capped at 50
                .andExpect(status().isOk());
    }
}