package com.recipes.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.recipes.entities.Chef;
import com.recipes.payload.response.ChefResponse;
import com.recipes.payload.response.MessageResponse;
import com.recipes.repositories.ChefRepository;
import com.recipes.security.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/chefs")
public class ChefController {

    @Autowired
    ChefRepository chefRepository;

    // Follow a chef
    @PostMapping("/{id}/follow")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> followChef(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the current chef (follower)
        Chef follower = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Current chef not found"));
        
        // Get the chef to follow
        Chef chefToFollow = chefRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chef to follow not found"));
        
        // Prevent self-following
        if (follower.getId().equals(chefToFollow.getId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("You cannot follow yourself"));
        }
        
        // Check if already following
        if (follower.getFollowing().contains(chefToFollow)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are already following this chef"));
        }
        
        // Add the follow relationship
        follower.getFollowing().add(chefToFollow);
        chefToFollow.getFollowers().add(follower);
        
        chefRepository.save(follower);
        chefRepository.save(chefToFollow);
        
        return ResponseEntity.ok(new MessageResponse("Successfully followed chef: " + chefToFollow.getHandle()));
    }

    // Unfollow a chef
    @DeleteMapping("/{id}/follow")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> unfollowChef(@PathVariable UUID id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        // Get the current chef (follower)
        Chef follower = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Current chef not found"));
        
        // Get the chef to unfollow
        Chef chefToUnfollow = chefRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chef to unfollow not found"));
        
        // Check if not following
        if (!follower.getFollowing().contains(chefToUnfollow)) {
            return ResponseEntity.badRequest().body(new MessageResponse("You are not following this chef"));
        }
        
        // Remove the follow relationship
        follower.getFollowing().remove(chefToUnfollow);
        chefToUnfollow.getFollowers().remove(follower);
        
        chefRepository.save(follower);
        chefRepository.save(chefToUnfollow);
        
        return ResponseEntity.ok(new MessageResponse("Successfully unfollowed chef: " + chefToUnfollow.getHandle()));
    }

    // Get followed chefs
    @GetMapping("/following")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> getFollowingChefs() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Chef chef = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));
        
        List<ChefResponse> following = chef.getFollowing().stream()
                .map(this::convertToChefResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(following);
    }

    // Get followers
    @GetMapping("/followers")
    @PreAuthorize("hasRole('CHEF')")
    public ResponseEntity<?> getFollowers() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        Chef chef = chefRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));
        
        List<ChefResponse> followers = chef.getFollowers().stream()
                .map(this::convertToChefResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(followers);
    }

    // Helper method to convert Chef entity to ChefResponse DTO
    private ChefResponse convertToChefResponse(Chef chef) {
        return new ChefResponse(
                chef.getId(),
                chef.getHandle(),
                chef.getName(),
                chef.getEmail(),
                chef.isVerified(),
                chef.getCreatedAt(),
                chef.getUpdatedAt()
        );
    }
}