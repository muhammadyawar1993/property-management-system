package com.property.management.models;

import jakarta.persistence.*;

@Entity
@Table(name = "property_interested")
public class PropertyInterested {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "house_id")
    private Long houseId;
    @Column(name = "buyer_id")
    private Long buyerId;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "uploaded_by")
    private String uploadedByType;
    @Column(name = "property_type")
    private String propertyType;
    @Column(name = "interested")
    private boolean interested;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUploadedByType() {
        return uploadedByType;
    }

    public void setUploadedByType(String uploadedByType) {
        this.uploadedByType = uploadedByType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public boolean isInterested() {
        return interested;
    }

    public void setInterested(boolean interested) {
        this.interested = interested;
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }
}
