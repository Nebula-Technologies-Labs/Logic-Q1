package com.logicq1.crm.modules.organization.controller;

import com.logicq1.crm.modules.organization.dto.OrganizationDto;
import com.logicq1.crm.modules.organization.service.OrganizationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/organization")
@AllArgsConstructor
public class OrganizationController {

    private OrganizationService organizationService;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAll() {
        HashMap<String, Object> response = new HashMap<>();
        List<OrganizationDto> organizations = organizationService.findAllOrganizations();
        response.put("organizations", organizations);
        response.put("status" , true);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
