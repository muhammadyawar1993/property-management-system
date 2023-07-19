package com.property.management.repository;

import com.property.management.models.House;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT p FROM House p WHERE p.houseId = :id AND p.sellerId = :sellerId")
    Optional<House> findByIdAndSellerId(@Param("id") Long id,
                                        @Param("sellerId") Long sellerId);

    List<House> findByPriceBetweenAndType(String minPrice, String maxPrice, String type);

    List<House> findByPriceBetween(String minPrice, String maxPrice);

    @Query("SELECT p FROM House p WHERE p.type = :type AND p.price >= :minPrice")
    List<House> findByTypeAndMinPrice(@Param("type") String type,
                                                 @Param("minPrice") String minPrice);

    @Query("SELECT p FROM House p WHERE p.type = :type AND p.price <= :maxPrice")
    List<House> findByTypeAndMaxPrice(@Param("type") String type,
                                                 @Param("maxPrice") String maxPrice);

    @Query("SELECT p FROM House p WHERE p.price >= :minPrice")
    List<House> findByMinPrice(@Param("minPrice") String minPrice);

    @Query("SELECT p FROM House p WHERE p.price <= :maxPrice")
    List<House> findByMaxPrice(@Param("maxPrice") String minPrice);

    List<House> findByType(String type);

    @Query("SELECT p FROM House p WHERE p.houseId = :id")
    Optional<House> findById(@Param("id") Long id);


    long countHouseBySellerId(Long sellerId);
}
