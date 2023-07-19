package com.property.management.controllers;

import com.property.management.mapper.SellerMapper;
import com.property.management.models.*;
import com.property.management.payload.request.HouseRequest;
import com.property.management.payload.response.CountResponse;
import com.property.management.payload.response.MessageResponse;
import com.property.management.payload.response.SellerResponse;
import com.property.management.payload.response.UserInfoResponse;
import com.property.management.repository.HouseRepository;
import com.property.management.repository.PropertyInterestedRepository;
import com.property.management.repository.UserRepository;
import com.property.management.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
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
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('ROLE_SELLER') or hasRole('ROLE_AGENT')")
public class AgentAndSellerController {

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private PropertyInterestedRepository propertyInterestedRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    SellerMapper sellerMapper;

    @PostMapping("/create")
    public ResponseEntity<?> addHouse(@Valid @RequestBody HouseRequest houseRequest) {
        Long id = 0L;
        if (ObjectUtils.isEmpty(houseRequest)) {
            return ResponseEntity.ok().body(new MessageResponse("Error: Request Body is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            id = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        House house = sellerMapper.requestToModel(houseRequest);
        house = sellerMapper.setSellerId(house, id);
        house = houseRepository.save(house);
        return ResponseEntity.ok().body(house);
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewHouse(@Valid @RequestParam(required = false) String id) {
        Long loggedInId = 0L;
        if (!StringUtils.hasLength(id)) {
            return ResponseEntity.ok().body(new MessageResponse("Error: Request param is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        Optional<House> house = houseRepository.findById(Long.valueOf(id));

        if (house.isPresent()) {
            return ResponseEntity.ok().body(house.get());
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: Property not found!"));
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteHouse(@Valid @RequestParam(required = false) String id) {
        Long loggedInId = 0L;
        if (!StringUtils.hasLength(id)) {
            return ResponseEntity.ok().body(new MessageResponse("Error: Request param is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        Optional<House> house = houseRepository.findById(Long.valueOf(id));

        if (house.isPresent()) {
            houseRepository.delete(house.get());
            return ResponseEntity.ok().body(new MessageResponse("Property Deleted Successfully!"));
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: Property not found!"));
        }
    }

    @GetMapping("/view-all")
    public ResponseEntity<?> viewAllHouse() {
        Long loggedInId = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        List<House> house = houseRepository.findBySellerId(loggedInId);

        if (!house.isEmpty()) {
            return ResponseEntity.ok().body(house);
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: Properties not found for this seller!"));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHouse(
            @PathVariable Long id,
            @RequestBody HouseRequest houseRequest) {

        Long loggedInId = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }
        // Retrieve the existing house from the database
        Optional<House> optionalHouse = houseRepository.findByIdAndSellerId(id, loggedInId);
        if (!optionalHouse.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        House existingHouse = optionalHouse.get();
        sellerMapper.updateHouseFromDTO(existingHouse, houseRequest);
        House savedHouse = houseRepository.save(existingHouse);
        return ResponseEntity.ok(savedHouse);
    }

    @GetMapping("/filter")
    public ResponseEntity<?> filterProperties(
            @RequestParam(value = "minPrice", required = false) String minPrice,
            @RequestParam(value = "maxPrice", required = false) String maxPrice,
            @RequestParam(value = "type", required = false) String type) {

        Long loggedInId = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.ok().body(new MessageResponse("Error: User is not Logged In!"));
        }

        List<House> houses;
        List<SellerResponse> sellerResponse;

        if (StringUtils.hasLength(type) && StringUtils.hasLength(minPrice) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByPriceBetweenAndTypeAndSellerId(minPrice, maxPrice, type, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type)) && StringUtils.hasLength(minPrice)) {
            houses = houseRepository.findByTypeAndMinPriceAndSellerId(type, minPrice, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type)) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByTypeAndMaxPriceAndSellerId(type, maxPrice, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((type))) {
            houses = houseRepository.findByTypeAndSellerId(type, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength((minPrice)) && StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByPriceBetweenAndSellerId(minPrice, maxPrice, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength(minPrice)) {
            houses = houseRepository.findByMinPriceAndSellerId(minPrice, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else if (StringUtils.hasLength(maxPrice)) {
            houses = houseRepository.findByMaxPriceAndSellerId(maxPrice, loggedInId);
            sellerResponse = getUserInfo(houses);
        } else {
            houses = houseRepository.findBySellerId(loggedInId);
            sellerResponse = getUserInfo(houses);
        }
        return ResponseEntity.ok().body(sellerResponse);
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

        // Count houses based on logged in user
        long houseCount = houseRepository.countHouseBySellerId(userId);

        // Count interested properties based on user_id and interested = true
        long interestedCount = propertyInterestedRepository.countByUserIdAndInterestedTrue(userId);

        // Create response object
        response.setHouseCount(houseCount);
        response.setInterestedCount(interestedCount);

        return ResponseEntity.ok(response);
    }

    private List<SellerResponse> getUserInfo(List<House> houses) {
        List<SellerResponse> sellerResponses = new ArrayList<>();
        Long id = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            id = ((UserDetailsImpl) (principal)).getId();
        }
        for (House house : houses) {
            SellerResponse sellerResponse = new SellerResponse();
            List<UserInfoResponse> userInfoResponses = new ArrayList<>();
            sellerResponse.setHouse(house);
            List<PropertyInterested> propertyInterested = propertyInterestedRepository.findByHouseIdAndUserId(house.getHouseId(), id);
            if (propertyInterested != null) {
                for (PropertyInterested propertyInterested1 : propertyInterested) {
                    User user = userRepository.findUserWithRolesById(propertyInterested1.getBuyerId()).orElse(null);
                    if (user != null) {
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
                        userInfoResponses.add(userInfoResponse);
                        if (ObjectUtils.isEmpty(propertyInterested1)) {
                            sellerResponse.setInterested(false);
                        } else {
                            sellerResponse.setInterested(propertyInterested1.isInterested());
                        }
                    }
                }
                sellerResponse.setUserInfoResponse(userInfoResponses);
            }
            sellerResponses.add(sellerResponse);
        }
        return sellerResponses;
    }
}
