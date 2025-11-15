package com.recipes.payload.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RecipeResponse {
    private UUID id;
    private String title;
    private String summary;
    private List<String> ingredients;
    private List<String> steps;
    private List<String> labels;
    private List<String> imageUrls;
    private String status;
    private LocalDateTime publishedAt;
    private ChefResponse author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public RecipeResponse() {}

    public RecipeResponse(UUID id, String title, String summary, List<String> ingredients, 
                         List<String> steps, List<String> labels, List<String> imageUrls, 
                         String status, LocalDateTime publishedAt, ChefResponse author, 
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.ingredients = ingredients;
        this.steps = steps;
        this.labels = labels;
        this.imageUrls = imageUrls;
        this.status = status;
        this.publishedAt = publishedAt;
        this.author = author;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public ChefResponse getAuthor() {
        return author;
    }

    public void setAuthor(ChefResponse author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}