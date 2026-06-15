package com.logicq1.crm.modules.task.service;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.lead.entity.Lead;
import com.logicq1.crm.modules.lead.repository.LeadRepository;
import com.logicq1.crm.modules.task.dto.*;
import com.logicq1.crm.modules.task.entity.Task;
import com.logicq1.crm.modules.task.mapper.TaskMapper;
import com.logicq1.crm.modules.task.repository.TaskRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final LeadRepository leadRepository;

    @Transactional
    public TaskDTO createTask(CreateTaskRequest request, User user) {
        if (request.subject() == null || request.subject().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task subject is required");
        }

        Task task = taskMapper.toEntity(request);

        task.setOrganization(user.getOrganization());
        task.setOwner(resolveOwner(request.ownerId(), user));
        task.setRelatedContact(resolveContact(request.relatedContactId(), user));
        task.setRelatedLead(resolveLead(request.relatedLeadId(), user));
        task.setCreatedBy(user);
        task.setUpdatedBy(user);

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NOT_STARTED);
        }
        if (task.getPriority() == null) {
            task.setPriority(TaskPriority.NORMAL);
        }

        // Append to the bottom of its status column.
        int position = (int) taskRepository.countByOrganizationIdAndStatus(
                user.getOrganization().getId(), task.getStatus());
        task.setBoardPosition(position);

        return taskMapper.toDTO(taskRepository.save(task));
    }

    public List<TaskDTO> listTasks(User user) {
        return taskRepository
                .findByOrganizationIdOrderByBoardPositionAsc(user.getOrganization().getId())
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public TaskDTO getTaskById(String id, User user) {
        return taskMapper.toDTO(loadOwnedTask(id, user));
    }

    /**
     * The kanban board: every status as a column with its cards ordered by position.
     */
    public List<TaskBoardColumn> getBoard(User user) {
        UUID orgId = user.getOrganization().getId();
        return Arrays.stream(TaskStatus.values())
                .map(status -> new TaskBoardColumn(
                        status,
                        taskRepository
                                .findByOrganizationIdAndStatusOrderByBoardPositionAsc(orgId, status)
                                .stream()
                                .map(taskMapper::toDTO)
                                .toList()
                ))
                .toList();
    }

    @Transactional
    public TaskDTO updateTask(String id, UpdateTaskRequest request, User user) {
        Task task = loadOwnedTask(id, user);

        if (request.subject() != null) {
            task.setSubject(request.subject());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.reminderAt() != null) {
            task.setReminderAt(request.reminderAt());
        }
        if (request.sendNotificationEmail() != null) {
            task.setSendNotificationEmail(request.sendNotificationEmail());
        }
        if (request.recurringActivity() != null) {
            task.setRecurringActivity(request.recurringActivity());
        }
        if (request.ownerId() != null) {
            task.setOwner(resolveOwner(request.ownerId(), user));
        }
        if (request.relatedContactId() != null) {
            task.setRelatedContact(resolveContact(request.relatedContactId(), user));
        }
        if (request.relatedLeadId() != null) {
            task.setRelatedLead(resolveLead(request.relatedLeadId(), user));
        }
        // A status change through update lands the card at the bottom of the new column.
        if (request.status() != null && request.status() != task.getStatus()) {
            moveToColumn(task, request.status(), Integer.MAX_VALUE, user);
        }

        task.setUpdatedBy(user);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    /**
     * Kanban drag-and-drop: move a card to a status column and a position within it.
     */
    @Transactional
    public TaskDTO moveTask(String id, MoveTaskRequest request, User user) {
        Task task = loadOwnedTask(id, user);
        TaskStatus target = request.status() != null ? request.status() : task.getStatus();
        int position = request.position() != null ? request.position() : 0;
        moveToColumn(task, target, position, user);
        task.setUpdatedBy(user);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(String id, User user) {
        Task task = loadOwnedTask(id, user);
        taskRepository.delete(task);
    }

    // --- helpers -------------------------------------------------------------

    /**
     * Place {@code task} into {@code target} column at {@code position}, renumbering
     * the column so positions stay contiguous. Works for both reorder-within-column
     * and cross-column moves.
     */
    private void moveToColumn(Task task, TaskStatus target, int position, User user) {
        UUID orgId = user.getOrganization().getId();
        List<Task> column = new ArrayList<>(
                taskRepository.findByOrganizationIdAndStatusOrderByBoardPositionAsc(orgId, target));
        column.removeIf(t -> t.getId().equals(task.getId()));

        int index = Math.max(0, Math.min(position, column.size()));
        task.setStatus(target);
        column.add(index, task);

        for (int i = 0; i < column.size(); i++) {
            column.get(i).setBoardPosition(i);
        }
        taskRepository.saveAll(column);
    }

    private Task loadOwnedTask(String id, User user) {
        UUID taskId;
        try {
            taskId = UUID.fromString(id);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid task id: '" + id + "' is not a valid UUID");
        }
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        validateOrganizationAccess(task, user);
        return task;
    }

    private User resolveOwner(UUID ownerId, User user) {
        if (ownerId == null) {
            return user;
        }
        User owner = userRepository
                .findByIdAndOrganizationId(ownerId, user.getOrganization().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Owner not found in your organization"));
        return owner;
    }

    private Contact resolveContact(UUID contactId, User user) {
        if (contactId == null) {
            return null;
        }
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Related contact not found"));
        if (!contact.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have access to this contact");
        }
        return contact;
    }

    private Lead resolveLead(UUID leadId, User user) {
        if (leadId == null) {
            return null;
        }
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Related lead not found"));
        if (!lead.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have access to this lead");
        }
        return lead;
    }

    private void validateOrganizationAccess(Task task, User user) {
        if (!task.getOrganization().getId().equals(user.getOrganization().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You do not have access to this task");
        }
    }
}
