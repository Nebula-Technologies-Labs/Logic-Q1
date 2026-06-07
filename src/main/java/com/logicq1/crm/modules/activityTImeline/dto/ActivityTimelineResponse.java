package com.logicq1.crm.modules.activityTImeline.dto;



import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTimelineResponse {

    private UUID id;
    private String entityType;
    private UUID entityId;
    private String actionType;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private UUID performedBy;
    private LocalDateTime createdAt;
}
