package com.recipes.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.recipes.entities.Chef;
import com.recipes.entities.Recipe;
import com.recipes.payload.request.RecipeRequest;
import com.recipes.payload.response.ChefResponse;
import com.recipes.payload.response.MessageResponse;
import com.recipes.payload.response.RecipeResponse;
import com.recipes.repositories.ChefRepository;
import com.recipes.repositories.RecipeRepository;
import com.recipes.security.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class RecipeController {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ChefRepository chefRepository;
    
    @Autowired
    RabbitTemplate rabbitTemplate;

    // Public endpoint to list recipes with filters
    @GetMapping("/public/recipes")
    public ResponseEntity<?> getPublicRecipes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String published_from,
            @RequestParam(required = false) String published_to,
            @RequestParam(required = false) String chef_id,
            @RequestParam(required = false) String chef_handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int page_size) {

        // Validate page size
        if (page_size > 50) {
            page_size = 50;
        }

        Pageable pageable = PageRequest.of(page, page_size, Sort.by("createdAt").descending());
        Page<Recipe> recipePage;
        
        // Parse dates if provided
        LocalDateTime fromDate = null;
        LocalDateTime toDate = null;
        
        if (published_from != null) {
            try {
                fromDate = LocalDateTime.parse(published_from, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid published_from date format. Use ISO format."));
            }
        }
        
        if (published_to != null) {
            try {
                toDate = LocalDateTime.parse(published_to, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid published_to date format. Use ISO format."));
            }
        }
        
        // Get chef ID if handle is provided
        UUID chefId = null;
        if (chef_handle != null) {
            var chefOpt = chefRepository.findByHandle(chef_handle);
            if (chefOpt.isPresent()) {
                chefId = chefOpt.get().getId();
            } else {
                // Return empty result if chef not found
                return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, page_size, 0, 0));
            }
        } else if (chef_id != null) {
            try {
                chefId = UUID.fromString(chef_id);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid chef_id format."));
            }
        }

        // Build query based on parameters
        if (q != null && !q.isEmpty()) {
            if (chefId != null) {
                recipePage = recipeRepository.findByStatusAndKeywordAndAuthorIn(
                    Recipe.RecipeStatus.PUBLISHED, q, List.of(chefId), pageable);
            } else if (fromDate != null || toDate != null) {
                if (fromDate == null) fromDate = LocalDateTime.MIN;
                if (toDate == null) toDate = LocalDateTime.MAX;
                
                recipePage = recipeRepository.findByStatusAndKeywordAndAuthorInAndCreatedAtBetween(
                    Recipe.RecipeStatus.PUBLISHED, q, new ArrayList<>(), fromDate, toDate, pageable);
            } else {
                recipePage = recipeRepository.findByStatusAndKeyword(
                    Recipe.RecipeStatus.PUBLISHED, q, pageable);
            }
        } else if (chefId != null) {
            recipePage = recipeRepository.findByStatusAndAuthorId(
                Recipe.RecipeStatus.PUBLISHED, chefId, pageable);
        } else if (fromDate != null || toDate != null) {
            if (fromDate == null) fromDate = LocalDateTime.MIN;
            if (toDate == null) toDate = LocalDateTime.MAX;
            
            recipePage = recipeRepository.findByStatusAndCreatedAtBetween(
                Recipe.RecipeStatus.PUBLISHED, fromDate, toDate, pageable);
        } else {
            recipePage = recipeRepository.findByStatus(Recipe.RecipeStatus.PUBLISHED, pageable);
        }

        // Convert to DTOs
        List<RecipeResponse> recipes = recipePage.getContent().stream()
                .map(this::convertToRecipeResponse)
                .collect(Collectors.toList());

        PageResponse<RecipeResponse> response = new PageResponse<>(
                recipes,
                recipePage.getNumber(),
                recipePage.getSize(),
                recipePage.getTotalElements(),
                recipePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // Create a new recipe
    @PostMapping("/recipes")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> createRecipe(@Valid @RequestBody RecipeRequest recipeRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chef author = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        Recipe recipe = new Recipe(recipeRequest.getTitle(), author);
        recipe.setSummary(recipeRequest.getSummary());
        recipe.setIngredients(recipeRequest.getIngredients());
        recipe.setSteps(recipeRequest.getSteps());
        recipe.setLabels(recipeRequest.getLabels() != null ? recipeRequest.getLabels() : new ArrayList<>());
        recipe.setStatus(Recipe.RecipeStatus.DRAFT);

        // Send to queue for async processing
        rabbitTemplate.convertAndSend("recipe.created", recipe);
        
        recipeRepository.save(recipe);

        return ResponseEntity.ok(convertToRecipeResponse(recipe));
    }

    // Publish a recipe
    @PutMapping("/recipes/{id}/publish")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> publishRecipe(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Check if chef owns the recipe or is admin
        if (!recipe.getAuthor().getId().equals(userDetails.getId())) {
            // Here we would check for admin role, but for simplicity we'll just deny
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Not authorized to publish this recipe"));
        }

        recipe.setStatus(Recipe.RecipeStatus.PUBLISHED);
        recipe.setPublishedAt(LocalDateTime.now());
        
        // Send to queue for async processing
        rabbitTemplate.convertAndSend("recipe.published", recipe);
        
        recipeRepository.save(recipe);

        return ResponseEntity.ok(convertToRecipeResponse(recipe));
    }

    // Update a recipe
    @PutMapping("/recipes/{id}")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> updateRecipe(@PathVariable UUID id, @Valid @RequestBody RecipeRequest recipeRequest) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Check if chef owns the recipe or is admin
        if (!recipe.getAuthor().getId().equals(userDetails.getId())) {
            // Here we would check for admin role, but for simplicity we'll just deny
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Not authorized to update this recipe"));
        }

        recipe.setTitle(recipeRequest.getTitle());
        recipe.setSummary(recipeRequest.getSummary());
        recipe.setIngredients(recipeRequest.getIngredients());
        recipe.setSteps(recipeRequest.getSteps());
        recipe.setLabels(recipeRequest.getLabels() != null ? recipeRequest.getLabels() : new ArrayList<>());
        
        // Send to queue for async processing
        rabbitTemplate.convertAndSend("recipe.updated", recipe);
        
        recipeRepository.save(recipe);

        return ResponseEntity.ok(convertToRecipeResponse(recipe));
    }

    // Delete a recipe
    @DeleteMapping("/recipes/{id}")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> deleteRecipe(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Check if chef owns the recipe or is admin
        if (!recipe.getAuthor().getId().equals(userDetails.getId())) {
            // Here we would check for admin role, but for simplicity we'll just deny
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse("Not authorized to delete this recipe"));
        }

        // Send to queue for async processing
        rabbitTemplate.convertAndSend("recipe.deleted", recipe.getId());
        
        recipeRepository.delete(recipe);

        return ResponseEntity.ok(new MessageResponse("Recipe deleted successfully"));
    }

    // Helper method to convert Recipe entity to RecipeResponse DTO
    private RecipeResponse convertToRecipeResponse(Recipe recipe) {
        Chef author = recipe.getAuthor();
        ChefResponse chefResponse = new ChefResponse(
                author.getId(),
                author.getHandle(),
                author.getName(),
                author.getEmail(),
                author.isVerified(),
                author.getCreatedAt(),
                author.getUpdatedAt()
        );

        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getSummary(),
                recipe.getIngredients(),
                recipe.getSteps(),
                recipe.getLabels(),
                recipe.getImageUrls(),
                recipe.getStatus().name(),
                recipe.getPublishedAt(),
                chefResponse,
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
        );
    }
}