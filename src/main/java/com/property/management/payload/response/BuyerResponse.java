package com.property.management.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.property.management.models.House;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyerResponse {
    private House house;
    private UserInfoResponse userInfoResponse;

    public UserInfoResponse getUserInfoResponse() {
        return userInfoResponse;
    }

    public void setUserInfoResponse(UserInfoResponse userInfoResponse) {
        this.userInfoResponse = userInfoResponse;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
}
