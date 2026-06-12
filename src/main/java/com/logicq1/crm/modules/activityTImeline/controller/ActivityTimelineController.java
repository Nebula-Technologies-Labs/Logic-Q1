package com.logicq1.crm.modules.activityTImeline.controller;


import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.service.ActivityTimelineService;
import com.logicq1.crm.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
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
    public List<ActivityTimeline> getOrganizationFeed(
            @AuthenticationPrincipal User user
    ) {
        return service.getOrganizationFeed(
                user.getOrganization().getId()
        );
    }

}
