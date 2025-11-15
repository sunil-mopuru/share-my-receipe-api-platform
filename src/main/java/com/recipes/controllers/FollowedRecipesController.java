package com.recipes.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.recipes.entities.Chef;
import com.recipes.entities.Recipe;
import com.recipes.payload.response.MessageResponse;
import com.recipes.payload.response.RecipeResponse;
import com.recipes.repositories.ChefRepository;
import com.recipes.repositories.RecipeRepository;
import com.recipes.security.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/followed-recipes")
public class FollowedRecipesController {

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    ChefRepository chefRepository;

    // Get recipes from followed chefs with filters
    @GetMapping
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> getFollowedChefsRecipes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String published_from,
            @RequestParam(required = false) String published_to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int page_size) {

        // Validate page size
        if (page_size > 50) {
            page_size = 50;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chef currentChef = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        // Get IDs of followed chefs
        List<UUID> followedChefIds = currentChef.getFollowing().stream()
                .map(Chef::getId)
                .collect(Collectors.toList());

        // If no followed chefs, return empty result
        if (followedChefIds.isEmpty()) {
            return ResponseEntity.ok(new PageResponse<>(new ArrayList<>(), page, page_size, 0, 0));
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

        // Build query based on parameters
        if (q != null && !q.isEmpty()) {
            if (fromDate != null || toDate != null) {
                if (fromDate == null) fromDate = LocalDateTime.MIN;
                if (toDate == null) toDate = LocalDateTime.MAX;

                recipePage = recipeRepository.findByStatusAndKeywordAndAuthorInAndCreatedAtBetween(
                    Recipe.RecipeStatus.PUBLISHED, q, followedChefIds, fromDate, toDate, pageable);
            } else {
                recipePage = recipeRepository.findByStatusAndKeywordAndAuthorIn(
                    Recipe.RecipeStatus.PUBLISHED, q, followedChefIds, pageable);
            }
        } else if (fromDate != null || toDate != null) {
            if (fromDate == null) fromDate = LocalDateTime.MIN;
            if (toDate == null) toDate = LocalDateTime.MAX;

            recipePage = recipeRepository.findByStatusAndAuthorInAndCreatedAtBetween(
                Recipe.RecipeStatus.PUBLISHED, followedChefIds, fromDate, toDate, pageable);
        } else {
            recipePage = recipeRepository.findByStatusAndAuthorIn(
                Recipe.RecipeStatus.PUBLISHED, followedChefIds, pageable);
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

    // Helper method to convert Recipe entity to RecipeResponse DTO
    private RecipeResponse convertToRecipeResponse(Recipe recipe) {
        Chef author = recipe.getAuthor();
        com.recipes.payload.response.ChefResponse chefResponse = new com.recipes.payload.response.ChefResponse(
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