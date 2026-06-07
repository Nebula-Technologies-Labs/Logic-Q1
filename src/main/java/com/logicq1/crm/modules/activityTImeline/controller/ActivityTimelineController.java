package com.logicq1.crm.modules.activityTImeline.controller;


import com.logicq1.crm.modules.activityTImeline.entity.ActivityTimeline;
import com.logicq1.crm.modules.activityTImeline.service.ActivityTimelineService;
import lombok.RequiredArgsConstructor;
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
}
