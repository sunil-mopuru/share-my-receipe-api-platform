package com.recipes.repositories;

import com.recipes.entities.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChefRepository extends JpaRepository<Chef, UUID> {
    Optional<Chef> findByEmail(String email);
    Optional<Chef> findByHandle(String handle);
    Boolean existsByEmail(String email);
    Boolean existsByHandle(String handle);
}