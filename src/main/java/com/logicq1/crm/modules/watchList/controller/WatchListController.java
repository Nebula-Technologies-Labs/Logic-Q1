package com.logicq1.crm.modules.watchList.controller;

import com.logicq1.crm.modules.watchList.dto.CreateWatchListRequest;
import com.logicq1.crm.modules.watchList.service.WatchListService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/watchList")
@AllArgsConstructor
public class WatchListController {

    //CRD

    private final WatchListService watchListService;

    @PostMapping("/create")
    public ResponseEntity<String> createQuery(
            @RequestBody CreateWatchListRequest request
            ) {
        return watchListService.createQuery(request);
    }

    @PostMapping("/readAll")
    public ResponseEntity<?> readQuery() {
        return watchListService.readAll();
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteQuery(@PathVariable UUID id) {
        return watchListService.deleteQuery(id);
    }

}
