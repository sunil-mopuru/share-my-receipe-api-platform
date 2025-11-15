package com.recipes.entities;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChefTest {

    @Test
    public void testChefCreation() {
        // Given
        String handle = "testchef";
        String name = "Test Chef";
        String email = "test@example.com";
        String password = "password123";

        // When
        Chef chef = new Chef(handle, name, email, password);

        // Then
        assertNotNull(chef.getId());
        assertEquals(handle, chef.getHandle());
        assertEquals(name, chef.getName());
        assertEquals(email, chef.getEmail());
        assertEquals(password, chef.getPassword());
        assertFalse(chef.isVerified());
        assertNotNull(chef.getCreatedAt());
        assertNotNull(chef.getUpdatedAt());
    }

    @Test
    public void testChefSettersAndGetters() {
        // Given
        Chef chef = new Chef();

        // When
        UUID id = UUID.randomUUID();
        String handle = "testchef";
        String name = "Test Chef";
        String email = "test@example.com";
        String password = "password123";
        boolean verified = true;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        
        chef.setId(id);
        chef.setHandle(handle);
        chef.setName(name);
        chef.setEmail(email);
        chef.setPassword(password);
        chef.setVerified(verified);
        chef.setCreatedAt(createdAt);
        chef.setUpdatedAt(updatedAt);

        // Then
        assertEquals(id, chef.getId());
        assertEquals(handle, chef.getHandle());
        assertEquals(name, chef.getName());
        assertEquals(email, chef.getEmail());
        assertEquals(password, chef.getPassword());
        assertEquals(verified, chef.isVerified());
        assertEquals(createdAt, chef.getCreatedAt());
        assertEquals(updatedAt, chef.getUpdatedAt());
    }

    @Test
    public void testChefRelationships() {
        // Given
        Chef chef = new Chef();
        Chef follower1 = new Chef("follower1", "Follower 1", "follower1@example.com", "password123");
        Chef follower2 = new Chef("follower2", "Follower 2", "follower2@example.com", "password123");
        
        Set<Chef> followers = new HashSet<>();
        followers.add(follower1);
        followers.add(follower2);

        // When
        chef.setFollowers(followers);

        // Then
        assertEquals(2, chef.getFollowers().size());
        assertTrue(chef.getFollowers().contains(follower1));
        assertTrue(chef.getFollowers().contains(follower2));
    }

    @Test
    public void testPrePersistAndPreUpdate() {
        // Given
        Chef chef = new Chef("testchef", "Test Chef", "test@example.com", "password123");

        // When
        // Simulate pre-persist (this is normally handled by JPA)
        chef.onCreate();
        
        // Then
        assertNotNull(chef.getCreatedAt());
        assertNotNull(chef.getUpdatedAt());
        assertEquals(chef.getCreatedAt(), chef.getUpdatedAt());
        
        // When
        // Simulate some time passing and then an update
        try {
            Thread.sleep(10); // Small delay to ensure timestamps are different
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        LocalDateTime beforeUpdate = chef.getUpdatedAt();
        chef.onUpdate();
        
        // Then
        assertTrue(chef.getUpdatedAt().isAfter(beforeUpdate));
    }
}