package com.sweetShop.Backend.repository;

import com.sweetShop.Backend.model.Sweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * The Sweet Repository.
 * This interface handles all the direct communication with the 'sweets' database table.
 * By extending 'JpaRepository', we get basic CRUD methods (save, delete, findById) for free!
 */
public interface SweetRepository extends JpaRepository<Sweet, Long> {

    /**
     * Finds a sweet specifically by its exact name.
     * We use 'Optional' because the sweet might not exist, and we want to avoid NullPointerExceptions.
     * Spring Data JPA automatically writes the SQL for this based on the method name!
     * SQL: SELECT * FROM sweets WHERE name = ?
     */
    Optional<Sweet> findByName(String name);

    /**
     * Advanced Custom Search.
     * This is a complex manual query (JPQL) to filter sweets.
     * It handles three optional filters at once:
     * 1. Query: Matches text inside the Name OR Category (Case-insensitive).
     * 2. MinPrice: Matches sweets strictly more expensive than this.
     * 3. MaxPrice: Matches sweets strictly cheaper than this.
     *
     * Logic: If a parameter is NULL (user didn't provide it), that part of the filter is ignored.
     */
    @Query("SELECT s FROM Sweet s WHERE " +
            // Search Logic: If 'query' is provided, check if Name OR Category contains it (ignoring case).
            "(:query IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.category) LIKE LOWER(CONCAT('%', :query, '%'))) " +

            // Price Filter Logic: If 'minPrice' is provided, only show items with price >= minPrice.
            "AND (:minPrice IS NULL OR s.price >= :minPrice) " +

            // Price Filter Logic: If 'maxPrice' is provided, only show items with price <= maxPrice.
            "AND (:maxPrice IS NULL OR s.price <= :maxPrice)")
    List<Sweet> searchSweets(@Param("query") String query,
                             @Param("minPrice") Double minPrice,
                             @Param("maxPrice") Double maxPrice);
}