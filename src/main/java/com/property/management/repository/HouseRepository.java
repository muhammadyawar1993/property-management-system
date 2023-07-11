package com.property.management.repository;

import com.property.management.models.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findBySellerId(Long id);

    List<House> findByPriceBetweenAndTypeAndSellerId(String minPrice, String maxPrice, String type, Long id);

    List<House> findByPriceBetweenAndSellerId(String minPrice, String maxPrice, Long id);

    @Query("SELECT p FROM House p WHERE p.type = :type AND p.price >= :minPrice AND p.sellerId = :sellerId")
    List<House> findByTypeAndMinPriceAndSellerId(@Param("type") String type,
                                                 @Param("minPrice") String minPrice,
                                                 @Param("sellerId") Long sellerId);

    @Query("SELECT p FROM House p WHERE p.type = :type AND p.price <= :maxPrice AND p.sellerId = :sellerId")
    List<House> findByTypeAndMaxPriceAndSellerId(@Param("type") String type,
                                                 @Param("maxPrice") String maxPrice,
                                                 @Param("sellerId") Long sellerId);

    @Query("SELECT p FROM House p WHERE p.price >= :minPrice AND p.sellerId = :sellerId")
    List<House> findByMinPriceAndSellerId(@Param("minPrice") String minPrice,
                                          @Param("sellerId") Long id);

    @Query("SELECT p FROM House p WHERE p.price <= :maxPrice AND p.sellerId = :sellerId")
    List<House> findByMaxPriceAndSellerId(@Param("maxPrice") String minPrice,
                                          @Param("sellerId") Long id);

    List<House> findByTypeAndSellerId(String type, Long id);
}
