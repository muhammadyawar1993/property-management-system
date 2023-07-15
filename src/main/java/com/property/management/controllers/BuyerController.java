package com.property.management.controllers;

import com.property.management.models.ERole;
import com.property.management.models.House;
import com.property.management.models.Role;
import com.property.management.models.User;
import com.property.management.payload.response.BuyerResponse;
import com.property.management.payload.response.UserInfoResponse;
import com.property.management.repository.HouseRepository;
import com.property.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private List<BuyerResponse> getUserInfo(List<House> houses) {
        List<BuyerResponse> buyerResponses = new ArrayList<>();
        for (House house : houses) {
            BuyerResponse buyerResponse = new BuyerResponse();
            buyerResponse.setHouse(house);
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
            buyerResponses.add(buyerResponse);
        }
        return buyerResponses;
    }
}
