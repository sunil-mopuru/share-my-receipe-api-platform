package com.recipes.repositories;

import com.recipes.entities.Recipe;
import com.recipes.entities.Chef;
import com.recipes.entities.Recipe.RecipeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    Page<Recipe> findByStatus(RecipeStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS(SELECT 1 FROM r.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "EXISTS(SELECT 1 FROM r.steps s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Recipe> findByStatusAndKeyword(@Param("status") RecipeStatus status, 
                                       @Param("keyword") String keyword, 
                                       Pageable pageable);
                                       
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND r.author.id = :authorId")
    Page<Recipe> findByStatusAndAuthorId(@Param("status") RecipeStatus status, 
                                        @Param("authorId") UUID authorId, 
                                        Pageable pageable);
                                        
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND r.author.id IN :followedChefIds")
    Page<Recipe> findByStatusAndAuthorIn(@Param("status") RecipeStatus status, 
                                        @Param("followedChefIds") List<UUID> followedChefIds, 
                                        Pageable pageable);
                                        
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND r.createdAt BETWEEN :fromDate AND :toDate")
    Page<Recipe> findByStatusAndCreatedAtBetween(@Param("status") RecipeStatus status, 
                                                @Param("fromDate") LocalDateTime fromDate, 
                                                @Param("toDate") LocalDateTime toDate, 
                                                Pageable pageable);
                                                
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS(SELECT 1 FROM r.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "EXISTS(SELECT 1 FROM r.steps s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "r.author.id IN :followedChefIds")
    Page<Recipe> findByStatusAndKeywordAndAuthorIn(@Param("status") RecipeStatus status, 
                                                  @Param("keyword") String keyword, 
                                                  @Param("followedChefIds") List<UUID> followedChefIds, 
                                                  Pageable pageable);
                                                  
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND r.author.id IN :followedChefIds AND r.createdAt BETWEEN :fromDate AND :toDate")
    Page<Recipe> findByStatusAndAuthorInAndCreatedAtBetween(@Param("status") RecipeStatus status, 
                                                           @Param("followedChefIds") List<UUID> followedChefIds, 
                                                           @Param("fromDate") LocalDateTime fromDate, 
                                                           @Param("toDate") LocalDateTime toDate, 
                                                           Pageable pageable);
                                                           
    @Query("SELECT r FROM Recipe r WHERE r.status = :status AND " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.summary) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "EXISTS(SELECT 1 FROM r.ingredients i WHERE LOWER(i) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "EXISTS(SELECT 1 FROM r.steps s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "r.author.id IN :followedChefIds AND r.createdAt BETWEEN :fromDate AND :toDate")
    Page<Recipe> findByStatusAndKeywordAndAuthorInAndCreatedAtBetween(@Param("status") RecipeStatus status, 
                                                                     @Param("keyword") String keyword,
                                                                     @Param("followedChefIds") List<UUID> followedChefIds, 
                                                                     @Param("fromDate") LocalDateTime fromDate, 
                                                                     @Param("toDate") LocalDateTime toDate, 
                                                                     Pageable pageable);
}