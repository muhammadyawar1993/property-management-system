package com.property.management.mapper;

import com.property.management.models.House;
import com.property.management.models.PropertyInterested;
import com.property.management.payload.request.HouseRequest;
import com.property.management.payload.request.PropertyInterestedRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PropertyInterestedMapper {

    PropertyInterestedMapper INSTANCE = Mappers.getMapper(PropertyInterestedMapper.class);

    PropertyInterested requestToModel(PropertyInterestedRequest propertyInterestedRequest);

    default PropertyInterested setBuyerId(PropertyInterested propertyInterested, Long buyerId) {
        propertyInterested.setBuyerId(buyerId);
        return propertyInterested;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePropertyFromDTO(@MappingTarget PropertyInterested propertyInterested, PropertyInterestedRequest propertyInterestedRequest);
}
