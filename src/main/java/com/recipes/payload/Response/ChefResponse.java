package com.recipes.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChefResponse {
    private UUID id;
    private String handle;
    private String name;
    private String email;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ChefResponse() {}

    public ChefResponse(UUID id, String handle, String name, String email, boolean verified, 
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.handle = handle;
        this.name = name;
        this.email = email;
        this.verified = verified;
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

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
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