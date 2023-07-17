package com.property.management.payload.response;

import com.property.management.models.House;

import java.util.List;

public class SellerResponse {
    private House house;
    private Boolean interested;
    private List<UserInfoResponse> userInfoResponse;

    public List<UserInfoResponse> getUserInfoResponse() {
        return userInfoResponse;
    }

    public void setUserInfoResponse(List<UserInfoResponse> userInfoResponse) {
        this.userInfoResponse = userInfoResponse;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Boolean getInterested() {
        return interested;
    }

    public void setInterested(Boolean interested) {
        this.interested = interested;
    }
}
