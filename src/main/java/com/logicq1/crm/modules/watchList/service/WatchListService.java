package com.logicq1.crm.modules.watchList.service;

import com.logicq1.crm.modules.watchList.dto.CreateWatchListRequest;
import com.logicq1.crm.modules.watchList.repository.WatchListRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WatchListService {

    private final WatchListRepository watchListRepository;

    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    @Modifying
    public ResponseEntity<String> createQuery(CreateWatchListRequest request) {


        watchListRepository.save(request.toEntity());

        return ResponseEntity.ok("Query created successfully");
    }

    public ResponseEntity<?> readAll() {

        return ResponseEntity.ok(watchListRepository.findAll());

    }

    public ResponseEntity<?> deleteQuery(UUID id) {
        if (watchListRepository.existsById(id)) {
            watchListRepository.deleteById(id);
            return ResponseEntity.ok("Query deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
