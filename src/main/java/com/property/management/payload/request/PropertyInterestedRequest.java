package com.property.management.payload.request;

public class PropertyInterestedRequest {
    private Long houseId;
    private Long userId;
    private Boolean interested;
    private String uploadedByType;
    private String propertyType;
    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getInterested() {
        return interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
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
}
