package com.logicq1.crm.modules.deals.controller;

import com.logicq1.crm.modules.deals.dto.CreateDealDto;
import com.logicq1.crm.modules.deals.dto.DealDto;
import com.logicq1.crm.modules.deals.dto.UpdateDealDto;
import com.logicq1.crm.modules.deals.service.DealService;
import com.logicq1.crm.modules.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deals")
@RequiredArgsConstructor
public class DealController {

    private final DealService dealService;

    @PostMapping
    public ResponseEntity<DealDto> createDeal(
            @Valid @RequestBody CreateDealDto request,
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                dealService.createDeal(request, user)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealDto> getDealById(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                dealService.getDealById(id, user)
        );
    }

    @GetMapping
    public ResponseEntity<List<DealDto>> getAllDeals(
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                dealService.getAllDeals(
                        user.getOrganization().getId()
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealDto> updateDeal(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateDealDto request,
            @AuthenticationPrincipal User user
    ) {

        return ResponseEntity.ok(
                dealService.updateDeal(id, request, user)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeal(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user
    ) {

        dealService.deleteDeal(id, user);

        return ResponseEntity.ok("Deal deleted successfully");
    }
}