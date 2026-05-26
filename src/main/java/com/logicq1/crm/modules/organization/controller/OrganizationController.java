package com.logicq1.crm.modules.organization.controller;

import com.logicq1.crm.modules.organization.dto.CreateOrganizationDto;
import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.service.OrganizationService;
import com.logicq1.crm.modules.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organization")
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;

    @PostMapping("/create")
    private ResponseEntity<Map<String, Object>> create(@AuthenticationPrincipal User user,@RequestBody CreateOrganizationDto data) {
        HashMap<String , Object> response = new HashMap<>();
        OrganizationDto organization = organizationService.createOrganization(data, user);
        response.put("organization", organization);
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    private ResponseEntity<Map<String, Object>> getOrganizationById(@PathVariable("id") String id) {
        HashMap<String,Object> response = new HashMap<>();
        OrganizationDto organization = organizationService.findOrganizationById(id);
        response.put("organization", organization);
        response.put("status", true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/all")
    private ResponseEntity<Map<String, Object>> getAll() {
        HashMap<String, Object> response = new HashMap<>();
        List<OrganizationDto> organizations = organizationService.findAllOrganizations();
        response.put("organizations", organizations);
        response.put("status" , true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
