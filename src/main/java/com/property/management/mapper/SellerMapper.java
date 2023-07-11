package com.property.management.mapper;

import com.property.management.models.House;
import com.property.management.payload.request.HouseRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SellerMapper {

    SellerMapper INSTANCE = Mappers.getMapper(SellerMapper.class);
    House requestToModel(HouseRequest houseRequest);

    default House setSellerId(House house, Long sellerId) {
        house.setSellerId(sellerId);
        return house;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateHouseFromDTO(@MappingTarget House house, HouseRequest houseRequest);



}
