package com.logicq1.crm.modules.watchList.dto;

import com.logicq1.crm.modules.watchList.entity.WatchList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWatchListRequest {

    private String name;

    private String email;

    private String phoneNumber;

    public WatchList toEntity() {
        WatchList watchList = new WatchList();
        watchList.setName(name);
        watchList.setEmail(email);
        watchList.setPhoneNumber(phoneNumber);
        return watchList;
    }

}