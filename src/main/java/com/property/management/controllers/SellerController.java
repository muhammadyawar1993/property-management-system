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

@CrossOrigin(origins = "*", maxAge = 3600)
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
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Request Body is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            id = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not Logged In!"));
        }
        House house = sellerMapper.requestToModel(houseRequest);
        house = sellerMapper.setSellerId(house, id);
        house = houseRepository.save(house);
        return ResponseEntity.badRequest().body(house);
    }

    @GetMapping("/view")
    public ResponseEntity<?> viewHouse(@Valid @RequestParam(required = false) String id) {
        Long loggedInId = 0L;
        if (!StringUtils.hasLength(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Request param is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not Logged In!"));
        }
        Optional<House> house = houseRepository.findById(Long.valueOf(id));

        if(house.isPresent()) {
            return ResponseEntity.badRequest().body(house.get());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Property not found!"));
        }
    }

    @GetMapping("/delete")
    public ResponseEntity<?> deleteHouse(@Valid @RequestParam(required = false) String id) {
        Long loggedInId = 0L;
        if (!StringUtils.hasLength(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Request param is Empty"));
        }
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not Logged In!"));
        }
        Optional<House> house = houseRepository.findById(Long.valueOf(id));

        if(house.isPresent()) {
            houseRepository.delete(house.get());
            return ResponseEntity.badRequest().body(new MessageResponse("Property Deleted Successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Property not found!"));
        }
    }

    @GetMapping("/view-all")
    public ResponseEntity<?> viewAllHouse() {
        Long loggedInId = 0L;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            loggedInId = ((UserDetailsImpl) (principal)).getId();
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User is not Logged In!"));
        }
        List<House> house = houseRepository.findBySellerId(loggedInId);

        if(!house.isEmpty()) {
            return ResponseEntity.badRequest().body(house);
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Properties not found for this seller!"));
        }
    }


}
