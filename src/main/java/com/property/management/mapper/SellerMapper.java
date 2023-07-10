package com.property.management.mapper;

import com.property.management.models.House;
import com.property.management.payload.request.HouseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);
    House requestToModel(HouseRequest houseRequest);

    default House setSellerId(House house, Long sellerId) {
        house.setSellerId(sellerId);
        return house;
    }


}
