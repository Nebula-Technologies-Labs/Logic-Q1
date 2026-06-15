package com.logicq1.crm.modules.task.repository;

import com.logicq1.crm.enums.TaskStatus;
import com.logicq1.crm.modules.task.entity.Task;
import com.logicq1.crm.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByOrganizationIdOrderByBoardPositionAsc(UUID organizationId);

    List<Task> findByOrganizationIdAndStatusOrderByBoardPositionAsc(
            UUID organizationId,
            TaskStatus status
    );

    List<Task> findByOwnerOrderByBoardPositionAsc(User owner);

    long countByOrganizationIdAndStatus(UUID organizationId, TaskStatus status);
}
