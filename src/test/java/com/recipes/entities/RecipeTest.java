package com.recipes.entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RecipeTest {

    @Test
    public void testRecipeCreation() {
        // Given
        String title = "Test Recipe";
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");

        // When
        Recipe recipe = new Recipe(title, author);

        // Then
        assertNotNull(recipe.getId());
        assertEquals(title, recipe.getTitle());
        assertEquals(author, recipe.getAuthor());
        assertEquals(Recipe.RecipeStatus.DRAFT, recipe.getStatus());
        assertNotNull(recipe.getCreatedAt());
        assertNotNull(recipe.getUpdatedAt());
        assertNull(recipe.getPublishedAt());
    }

    @Test
    public void testRecipeSettersAndGetters() {
        // Given
        Recipe recipe = new Recipe();
        
        // When
        UUID id = UUID.randomUUID();
        String title = "Test Recipe";
        String summary = "This is a test recipe";
        List<String> ingredients = new ArrayList<>();
        ingredients.add("ingredient1");
        ingredients.add("ingredient2");
        
        List<String> steps = new ArrayList<>();
        steps.add("step1");
        steps.add("step2");
        
        List<String> labels = new ArrayList<>();
        labels.add("label1");
        labels.add("label2");
        
        List<String> imageUrls = new ArrayList<>();
        imageUrls.add("http://example.com/image1.jpg");
        imageUrls.add("http://example.com/image2.jpg");
        
        Recipe.RecipeStatus status = Recipe.RecipeStatus.PUBLISHED;
        LocalDateTime publishedAt = LocalDateTime.now();
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        recipe.setId(id);
        recipe.setTitle(title);
        recipe.setSummary(summary);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setLabels(labels);
        recipe.setImageUrls(imageUrls);
        recipe.setStatus(status);
        recipe.setPublishedAt(publishedAt);
        recipe.setAuthor(author);
        recipe.setCreatedAt(createdAt);
        recipe.setUpdatedAt(updatedAt);

        // Then
        assertEquals(id, recipe.getId());
        assertEquals(title, recipe.getTitle());
        assertEquals(summary, recipe.getSummary());
        assertEquals(ingredients, recipe.getIngredients());
        assertEquals(steps, recipe.getSteps());
        assertEquals(labels, recipe.getLabels());
        assertEquals(imageUrls, recipe.getImageUrls());
        assertEquals(status, recipe.getStatus());
        assertEquals(publishedAt, recipe.getPublishedAt());
        assertEquals(author, recipe.getAuthor());
        assertEquals(createdAt, recipe.getCreatedAt());
        assertEquals(updatedAt, recipe.getUpdatedAt());
    }

    @Test
    public void testRecipeStatusEnum() {
        // When & Then
        assertEquals("DRAFT", Recipe.RecipeStatus.DRAFT.name());
        assertEquals("PUBLISHED", Recipe.RecipeStatus.PUBLISHED.name());
    }

    @Test
    public void testPrePersistAndPreUpdate() {
        // Given
        Chef author = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        Recipe recipe = new Recipe("Test Recipe", author);

        // When
        // Simulate pre-persist (this is normally handled by JPA)
        recipe.onCreate();
        
        // Then
        assertNotNull(recipe.getCreatedAt());
        assertNotNull(recipe.getUpdatedAt());
        assertEquals(recipe.getCreatedAt(), recipe.getUpdatedAt());
        
        // When
        // Simulate some time passing and then an update
        try {
            Thread.sleep(10); // Small delay to ensure timestamps are different
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        LocalDateTime beforeUpdate = recipe.getUpdatedAt();
        recipe.onUpdate();
        
        // Then
        assertTrue(recipe.getUpdatedAt().isAfter(beforeUpdate));
    }
}