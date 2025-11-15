package com.recipes.repositories;

import com.recipes.entities.Chef;
import com.recipes.entities.Recipe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RecipeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    public void testFindByStatus() {
        // Given
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(author);
        
        Recipe recipe1 = new Recipe("Recipe 1", author);
        recipe1.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe1);
        
        Recipe recipe2 = new Recipe("Recipe 2", author);
        recipe2.setStatus(Recipe.RecipeStatus.DRAFT);
        entityManager.persistAndFlush(recipe2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Recipe> publishedRecipes = recipeRepository.findByStatus(Recipe.RecipeStatus.PUBLISHED, pageable);

        // Then
        assertEquals(1, publishedRecipes.getTotalElements());
        assertEquals("Recipe 1", publishedRecipes.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByStatusAndKeyword() {
        // Given
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(author);
        
        Recipe recipe1 = new Recipe("Chocolate Cake", author);
        recipe1.setSummary("Delicious chocolate cake recipe");
        List<String> ingredients1 = new ArrayList<>();
        ingredients1.add("flour");
        ingredients1.add("chocolate");
        recipe1.setIngredients(ingredients1);
        recipe1.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe1);
        
        Recipe recipe2 = new Recipe("Vegetable Soup", author);
        recipe2.setSummary("Healthy vegetable soup");
        List<String> ingredients2 = new ArrayList<>();
        ingredients2.add("carrots");
        ingredients2.add("potatoes");
        recipe2.setIngredients(ingredients2);
        recipe2.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Recipe> chocolateRecipes = recipeRepository.findByStatusAndKeyword(
            Recipe.RecipeStatus.PUBLISHED, "chocolate", pageable);

        // Then
        assertEquals(1, chocolateRecipes.getTotalElements());
        assertEquals("Chocolate Cake", chocolateRecipes.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByStatusAndAuthorId() {
        // Given
        Chef author1 = new Chef("testchef1", "Test Chef 1", "test1@example.com", "password123");
        entityManager.persistAndFlush(author1);
        
        Chef author2 = new Chef("testchef2", "Test Chef 2", "test2@example.com", "password123");
        entityManager.persistAndFlush(author2);
        
        Recipe recipe1 = new Recipe("Recipe 1", author1);
        recipe1.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe1);
        
        Recipe recipe2 = new Recipe("Recipe 2", author2);
        recipe2.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Recipe> author1Recipes = recipeRepository.findByStatusAndAuthorId(
            Recipe.RecipeStatus.PUBLISHED, author1.getId(), pageable);

        // Then
        assertEquals(1, author1Recipes.getTotalElements());
        assertEquals("Recipe 1", author1Recipes.getContent().get(0).getTitle());
    }

    @Test
    public void testFindByStatusAndCreatedAtBetween() {
        // Given
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(author);
        
        Recipe recipe1 = new Recipe("Recipe 1", author);
        recipe1.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe1);
        
        // Wait a bit to create a time difference
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        LocalDateTime fromDate = LocalDateTime.now();
        
        Recipe recipe2 = new Recipe("Recipe 2", author);
        recipe2.setStatus(Recipe.RecipeStatus.PUBLISHED);
        entityManager.persistAndFlush(recipe2);
        
        LocalDateTime toDate = LocalDateTime.now().plusDays(1);
        
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Recipe> recipes = recipeRepository.findByStatusAndCreatedAtBetween(
            Recipe.RecipeStatus.PUBLISHED, fromDate, toDate, pageable);

        // Then
        assertEquals(1, recipes.getTotalElements());
        assertEquals("Recipe 2", recipes.getContent().get(0).getTitle());
    }
}