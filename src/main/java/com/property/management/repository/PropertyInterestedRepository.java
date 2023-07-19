package com.property.management.repository;

import com.property.management.models.PropertyInterested;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyInterestedRepository extends JpaRepository<PropertyInterested, Long> {

    Optional<PropertyInterested> findByHouseIdAndUserIdAndBuyerId(Long houseId, Long userId, Long buyerId);

    Optional<PropertyInterested> findByHouseIdAndBuyerId(Long houseId, Long buyerId);

    List<PropertyInterested> findByHouseIdAndUserId(Long houseId, Long userId);

    long countByUserIdAndInterestedTrue(Long userId);

    long countByBuyerIdAndInterestedTrue(Long buyerId);

}
