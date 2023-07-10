package com.property.management.repository;

import com.property.management.models.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    List<House> findBySellerId(Long id);
    List<House> findByPriceGreaterThanEqualAndPriceLessThanEqualAndSellerId(String priceFrom, String priceTo, Long id);
}
