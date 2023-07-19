package com.property.management.controllers;

import com.property.management.mapper.PropertyInterestedMapper;
import com.property.management.models.*;
import com.property.management.payload.request.PropertyInterestedRequest;
import com.property.management.payload.response.BuyerResponse;
import com.property.management.payload.response.CountResponse;
import com.property.management.payload.response.MessageResponse;
import com.property.management.payload.response.UserInfoResponse;
import com.property.management.repository.HouseRepository;
import com.property.management.repository.PropertyInterestedRepository;
import com.property.management.repository.UserRepository;
import com.property.management.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/buyer")
@PreAuthorize("hasRole('ROLE_BUYER')")
public class BuyerController {
    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PropertyInterestedRepository propertyInterestedRepository;

    @Autowired
    private PropertyInterestedMapper propertyInterestedMapper;

    @PostMapping("/show-interest")
    public ResponseEntity<?> saveOrUpdateInterest(@RequestBody PropertyInterestedRequest propertyInterestedRequest) {
        Long id = 0L;
        if (ObjectUtils.isEmpty(propertyInterestedRequest)) {
            return ResponseEntity.ok().body(new MessageResponse("Error: Request Body is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            id = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        House house = houseRepository.findById(propertyInterestedRequest.getHouseId()).orElse(null);
        if (ObjectUtils.isEmpty(house)) {
            return ResponseEntity.ok().body(new MessageResponse("Error: Invalid Property not found"));
        } else {
            PropertyInterested propertyInterested = propertyInterestedRepository.findByHouseIdAndUserIdAndBuyerId(propertyInterestedRequest.getHouseId(), propertyInterestedRequest.getUserId(), id).orElse(null);
            if (ObjectUtils.isEmpty(propertyInterested)) {
                PropertyInterested property = propertyInterestedMapper.requestToModel(propertyInterestedRequest);
                property = propertyInterestedMapper.setBuyerId(property, id);
                PropertyInterested propertySaved = propertyInterestedRepository.save(property);
                return ResponseEntity.ok(propertySaved);
            } else {
                propertyInterestedMapper.updatePropertyFromDTO(propertyInterested, propertyInterestedRequest);
                PropertyInterested propertySaved = propertyInterestedRepository.save(propertyInterested);
                return ResponseEntity.ok(propertySaved);
            }
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProperties(
            @RequestParam(value = "minPrice", required = false) String minPrice,
            @RequestParam(value = "maxPrice", required = false) String maxPrice,
            @RequestParam(value = "type", required = false) String type) {

        List<House> houses;
        List<BuyerResponse> buyerResponse;
        if (StringUtils.hasLength(type) && StringUtils.hasLength(minPrice) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByPriceBetweenAndType(minPrice, maxPrice, type);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type)) && StringUtils.hasLength(minPrice)) {
            houses = houseRepository.findByTypeAndMinPrice(type, minPrice);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type)) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByTypeAndMaxPrice(type, maxPrice);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type))) {
            houses = houseRepository.findByType(type);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((minPrice)) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByPriceBetween(minPrice, maxPrice);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength(minPrice)) {
            houses = houseRepository.findByMinPrice(minPrice);
            buyerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByMaxPrice(maxPrice);
            buyerResponse = getUserInfo(houses);
        } else {
            houses = houseRepository.findAll();
            buyerResponse = getUserInfo(houses);
        }
        return ResponseEntity.ok().body(buyerResponse);
    }

    @GetMapping("/dashboard/count")
    public ResponseEntity<?> getCountByUserId() {
        Long userId = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CountResponse response = new CountResponse();
        if (principal instanceof UserDetails) {
            userId = ((UserDetailsImpl) (principal)).getId();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            UserInfoResponse userInfoResponse = new UserInfoResponse(userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles, userDetails.getPhoneNumber());
            response.setUserInfoResponse(userInfoResponse);
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }

        // Count interested properties based on user_id and interested = true
        long interestedCount = propertyInterestedRepository.countByBuyerIdAndInterestedTrue(userId);

        // Create response object
        response.setHouseCount(null);
        response.setInterestedCount(interestedCount);

        return ResponseEntity.ok(response);
    }

    private List<BuyerResponse> getUserInfo(List<House> houses) {
        List<BuyerResponse> buyerResponses = new ArrayList<>();
        Long id = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            id = ((UserDetailsImpl) (principal)).getId();
        }
        for (House house : houses) {
            BuyerResponse buyerResponse = new BuyerResponse();
            buyerResponse.setHouse(house);
            PropertyInterested propertyInterested = propertyInterestedRepository.findByHouseIdAndBuyerId(house.getHouseId(), id).orElse(null);
            Long userId = house.getSellerId();
            User user = userRepository.findUserWithRolesById(userId).orElse(null);
            assert user != null;
            Iterator<Role> iterator = user.getRoles().iterator();
            List<ERole> enumRoles = new ArrayList<>();
            while (iterator.hasNext()) {
                Role element = iterator.next();
                ERole roleName = element.getName();
                enumRoles.add(roleName);
            }
            List<String> roles = enumRoles.stream()
                    .map(Enum::toString)
                    .collect(Collectors.toList());
            UserInfoResponse userInfoResponse = new UserInfoResponse(user.getUsername(), user.getEmail(), user.getPhoneNumber(), roles);
            buyerResponse.setUserInfoResponse(userInfoResponse);
            if (ObjectUtils.isEmpty(propertyInterested)) {
                buyerResponse.setInterested(false);
            } else {
                buyerResponse.setInterested(propertyInterested.isInterested());
            }
            buyerResponses.add(buyerResponse);
        }
        return buyerResponses;
    }


}
