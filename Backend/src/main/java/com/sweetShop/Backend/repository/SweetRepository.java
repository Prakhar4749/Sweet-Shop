package com.sweetShop.Backend.repository;

import com.sweetShop.Backend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SweetRepository extends JpaRepository<Sweet, Long> {
    Optional<Sweet> findByName(String name);

    // Advanced Search: Name OR Category, AND optional Price Range
    @Query("SELECT s FROM Sweet s WHERE " +
            "(:query IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:minPrice IS NULL OR s.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<Sweet> searchSweets(@Param("query") String query,
                             @Param("minPrice") Double minPrice,
                             @Param("maxPrice") Double maxPrice);
}