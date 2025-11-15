package com.recipes.repositories;

import com.recipes.entities.Chef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ChefRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChefRepository chefRepository;

    @Test
    public void testFindByEmail() {
        // Given
        Chef chef = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(chef);

        // When
        Optional<Chef> found = chefRepository.findByEmail("test@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals(chef.getEmail(), found.get().getEmail());
    }

    @Test
    public void testFindByEmailNotFound() {
        // When
        Optional<Chef> found = chefRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    public void testFindByHandle() {
        // Given
        Chef chef = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(chef);

        // When
        Optional<Chef> found = chefRepository.findByHandle("testchef");

        // Then
        assertTrue(found.isPresent());
        assertEquals(chef.getHandle(), found.get().getHandle());
    }

    @Test
    public void testExistsByEmail() {
        // Given
        Chef chef = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(chef);

        // When
        Boolean exists = chefRepository.existsByEmail("test@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    public void testExistsByEmailNotFound() {
        // When
        Boolean exists = chefRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertFalse(exists);
    }

    @Test
    public void testExistsByHandle() {
        // Given
        Chef chef = new Chef("testchef", "Test Chef", "test@example.com", "password123");
        entityManager.persistAndFlush(chef);

        // When
        Boolean exists = chefRepository.existsByHandle("testchef");

        // Then
        assertTrue(exists);
    }

    @Test
    public void testExistsByHandleNotFound() {
        // When
        Boolean exists = chefRepository.existsByHandle("nonexistent");

        // Then
        assertFalse(exists);
    }
}