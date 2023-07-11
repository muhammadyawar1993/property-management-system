package com.property.management.controllers;

import com.property.management.mapper.SellerMapper;
import com.property.management.models.House;
import com.property.management.payload.request.HouseRequest;
import com.property.management.payload.response.MessageResponse;
import com.property.management.repository.HouseRepository;
import com.property.management.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/seller")
@PreAuthorize("hasRole('ROLE_SELLER')")
public class SellerController {

    @Autowired
    private HouseRepository houseRepository;

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

        if(house.isPresent()) {
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

        if(house.isPresent()) {
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

        if(!house.isEmpty()) {
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
        if (type != null && minPrice != null && maxPrice != null) {
            houses = houseRepository.findByPriceBetweenAndTypeAndSellerId(minPrice, maxPrice, type, loggedInId);
        } else if (type != null && minPrice != null) {
            houses = houseRepository.findByTypeAndMinPriceAndSellerId(type, minPrice, loggedInId);
        } else if (type != null && maxPrice != null) {
            houses = houseRepository.findByTypeAndMaxPriceAndSellerId(type, maxPrice, loggedInId);
        } else if (type != null) {
            houses = houseRepository.findByTypeAndSellerId(type, loggedInId);
        } else if (minPrice != null && maxPrice != null) {
            houses = houseRepository.findByPriceBetweenAndSellerId(minPrice, maxPrice, loggedInId);
        } else if (minPrice != null) {
            houses = houseRepository.findByMinPriceAndSellerId(minPrice, loggedInId);
        } else if (maxPrice != null) {
            houses = houseRepository.findByMaxPriceAndSellerId(maxPrice, loggedInId);
        } else {
            houses = houseRepository.findBySellerId(loggedInId);
        }
        return ResponseEntity.ok().body(houses);
    }
}
