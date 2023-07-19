package com.property.management.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountResponse {
    private UserInfoResponse userInfoResponse;
    private Long houseCount;
    private Long interestedCount;


    public CountResponse() {

    }

    public UserInfoResponse getUserInfoResponse() {
        return userInfoResponse;
    }

    public void setUserInfoResponse(UserInfoResponse userInfoResponse) {
        this.userInfoResponse = userInfoResponse;
    }

    public Long getHouseCount() {
        return houseCount;
    }

    public void setHouseCount(Long houseCount) {
        this.houseCount = houseCount;
    }

    public Long getInterestedCount() {
        return interestedCount;
    }

    public void setInterestedCount(Long interestedCount) {
        this.interestedCount = interestedCount;
    }
}
