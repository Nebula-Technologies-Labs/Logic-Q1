package com.logicq1.crm.modules.activityTImeline.controller;


import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.service.ActivityTimelineService;
import com.logicq1.crm.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activity-timeline")
@RequiredArgsConstructor
public class ActivityTimelineController {

    private final ActivityTimelineService service;

    @GetMapping
    public List<ActivityTimeline> getTimeline(
            @RequestParam String entityType,
            @RequestParam UUID entityId
    ) {

        return service.getTimeline(
                entityType,
                entityId
        );
    }

    @GetMapping("/organization")
    public Page<ActivityTimeline> getOrganizationFeed(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return service.getOrganizationFeed(
                user.getOrganization().getId(),
                page,
                size
        );
    }

}
