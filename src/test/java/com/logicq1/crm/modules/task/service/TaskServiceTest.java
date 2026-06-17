package com.logicq1.crm.modules.task.service;

import com.logicq1.crm.enums.TaskPriority;
import com.logicq1.crm.enums.TaskStatus;
import com.logicq1.crm.modules.contacts.entity.Contact;
import com.logicq1.crm.modules.contacts.repository.ContactRepository;
import com.logicq1.crm.modules.lead.entity.Lead;
import com.logicq1.crm.modules.lead.repository.LeadRepository;
import com.logicq1.crm.modules.organization.entity.Organization;
import com.logicq1.crm.modules.task.dto.CreateTaskRequest;
import com.logicq1.crm.modules.task.dto.MoveTaskRequest;
import com.logicq1.crm.modules.task.dto.TaskBoardColumn;
import com.logicq1.crm.modules.task.dto.TaskDTO;
import com.logicq1.crm.modules.task.dto.UpdateTaskRequest;
import com.logicq1.crm.modules.task.entity.Task;
import com.logicq1.crm.modules.task.mapper.TaskMapper;
import com.logicq1.crm.modules.task.repository.TaskRepository;
import com.logicq1.crm.modules.user.entity.User;
import com.logicq1.crm.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = TaskService.class)
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;
    @MockitoBean
    private TaskMapper taskMapper;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private ContactRepository contactRepository;
    @MockitoBean
    private LeadRepository leadRepository;

    private Organization organization;
    private User user;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(UUID.randomUUID())
                .name("Acme")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .organization(organization)
                .build();
    }

    private CreateTaskRequest createRequest(String subject) {
        return new CreateTaskRequest(
                subject, "desc", null, null, null,
                null, null, null, null, null, null
        );
    }

    private TaskDTO sampleDto(UUID id) {
        return new TaskDTO(
                id, "subject", "desc", TaskStatus.NOT_STARTED, TaskPriority.NORMAL,
                null, null, null, null, 0,
                user.getId(), "John Doe", organization.getId(),
                null, null, null, null,
                user.getId(), user.getId(), null, null
        );
    }

    private Task taskInOrg(UUID id, Organization org) {
        Task task = new Task();
        task.setId(id);
        task.setOrganization(org);
        task.setStatus(TaskStatus.NOT_STARTED);
        task.setBoardPosition(0);
        return task;
    }

    @Test
    void createTask_persistsWithOwnerOrganizationAndDefaults() {
        CreateTaskRequest request = createRequest("Follow up");
        Task mapped = new Task();
        TaskDTO dto = sampleDto(UUID.randomUUID());

        when(taskMapper.toEntity(request)).thenReturn(mapped);
        when(taskRepository.countByOrganizationIdAndStatus(organization.getId(), TaskStatus.NOT_STARTED))
                .thenReturn(3L);
        when(taskRepository.save(mapped)).thenReturn(mapped);
        when(taskMapper.toDTO(mapped)).thenReturn(dto);

        TaskDTO result = taskService.createTask(request, user);

        assertThat(result).isEqualTo(dto);
        assertThat(mapped.getOwner()).isEqualTo(user);
        assertThat(mapped.getOrganization()).isEqualTo(organization);
        assertThat(mapped.getCreatedBy()).isEqualTo(user);
        assertThat(mapped.getUpdatedBy()).isEqualTo(user);
        assertThat(mapped.getStatus()).isEqualTo(TaskStatus.NOT_STARTED);
        assertThat(mapped.getPriority()).isEqualTo(TaskPriority.NORMAL);
        assertThat(mapped.getBoardPosition()).isEqualTo(3);
        verify(taskRepository).save(mapped);
    }

    @Test
    void createTask_keepsProvidedStatusAndPriority() {
        CreateTaskRequest request = createRequest("Call client");
        Task mapped = new Task();
        mapped.setStatus(TaskStatus.IN_PROGRESS);
        mapped.setPriority(TaskPriority.HIGH);

        when(taskMapper.toEntity(request)).thenReturn(mapped);
        when(taskRepository.countByOrganizationIdAndStatus(organization.getId(), TaskStatus.IN_PROGRESS))
                .thenReturn(0L);
        when(taskRepository.save(mapped)).thenReturn(mapped);
        when(taskMapper.toDTO(mapped)).thenReturn(sampleDto(UUID.randomUUID()));

        taskService.createTask(request, user);

        assertThat(mapped.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(mapped.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(mapped.getBoardPosition()).isEqualTo(0);
    }

    @Test
    void createTask_throwsBadRequest_whenSubjectIsNull() {
        CreateTaskRequest request = createRequest(null);

        assertThatThrownBy(() -> taskService.createTask(request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_throwsBadRequest_whenSubjectIsBlank() {
        CreateTaskRequest request = createRequest("   ");

        assertThatThrownBy(() -> taskService.createTask(request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTask_resolvesOwnerFromRequest_whenOwnerIdProvided() {
        UUID ownerId = UUID.randomUUID();
        User owner = User.builder().id(ownerId).organization(organization).build();
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, null, null, null,
                null, null, null, ownerId, null, null
        );
        Task mapped = new Task();

        when(taskMapper.toEntity(request)).thenReturn(mapped);
        when(userRepository.findByIdAndOrganizationId(ownerId, organization.getId()))
                .thenReturn(Optional.of(owner));
        when(taskRepository.countByOrganizationIdAndStatus(any(), any())).thenReturn(0L);
        when(taskRepository.save(mapped)).thenReturn(mapped);
        when(taskMapper.toDTO(mapped)).thenReturn(sampleDto(UUID.randomUUID()));

        taskService.createTask(request, user);

        assertThat(mapped.getOwner()).isEqualTo(owner);
    }

    @Test
    void createTask_throwsBadRequest_whenOwnerNotInOrganization() {
        UUID ownerId = UUID.randomUUID();
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, null, null, null,
                null, null, null, ownerId, null, null
        );

        when(taskMapper.toEntity(request)).thenReturn(new Task());
        when(userRepository.findByIdAndOrganizationId(ownerId, organization.getId()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.createTask(request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createTask_resolvesRelatedContact_whenSameOrganization() {
        UUID contactId = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setOrganization(organization);
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, null, null, null,
                null, null, null, null, contactId, null
        );
        Task mapped = new Task();

        when(taskMapper.toEntity(request)).thenReturn(mapped);
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(taskRepository.countByOrganizationIdAndStatus(any(), any())).thenReturn(0L);
        when(taskRepository.save(mapped)).thenReturn(mapped);
        when(taskMapper.toDTO(mapped)).thenReturn(sampleDto(UUID.randomUUID()));

        taskService.createTask(request, user);

        assertThat(mapped.getRelatedContact()).isEqualTo(contact);
    }

    @Test
    void createTask_throwsForbidden_whenRelatedContactBelongsToAnotherOrganization() {
        UUID contactId = UUID.randomUUID();
        Contact contact = new Contact();
        contact.setOrganization(Organization.builder().id(UUID.randomUUID()).build());
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, null, null, null,
                null, null, null, null, contactId, null
        );

        when(taskMapper.toEntity(request)).thenReturn(new Task());
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        assertThatThrownBy(() -> taskService.createTask(request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void createTask_throwsForbidden_whenRelatedLeadBelongsToAnotherOrganization() {
        UUID leadId = UUID.randomUUID();
        Lead lead = new Lead();
        lead.setOrganization(Organization.builder().id(UUID.randomUUID()).build());
        CreateTaskRequest request = new CreateTaskRequest(
                "Task", null, null, null, null,
                null, null, null, null, null, leadId
        );

        when(taskMapper.toEntity(request)).thenReturn(new Task());
        when(leadRepository.findById(leadId)).thenReturn(Optional.of(lead));

        assertThatThrownBy(() -> taskService.createTask(request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void listTasks_mapsEveryTask() {
        when(taskRepository.findByOrganizationIdOrderByBoardPositionAsc(organization.getId()))
                .thenReturn(List.of(new Task(), new Task()));
        when(taskMapper.toDTO(any())).thenReturn(sampleDto(UUID.randomUUID()));

        assertThat(taskService.listTasks(user)).hasSize(2);
    }

    @Test
    void getTaskById_returnsMappedDto_whenFound() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);
        TaskDTO dto = sampleDto(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(dto);

        assertThat(taskService.getTaskById(id.toString(), user)).isEqualTo(dto);
    }

    @Test
    void getTaskById_throwsBadRequest_whenIdIsNotUuid() {
        assertThatThrownBy(() -> taskService.getTaskById("not-a-uuid", user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getTaskById_throwsNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(id.toString(), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getTaskById_throwsForbidden_whenTaskBelongsToAnotherOrganization() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, Organization.builder().id(UUID.randomUUID()).build());

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.getTaskById(id.toString(), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getBoard_returnsAColumnPerStatus() {
        List<TaskBoardColumn> board = taskService.getBoard(user);

        assertThat(board).hasSize(TaskStatus.values().length);
        assertThat(board).extracting(TaskBoardColumn::status)
                .containsExactly(TaskStatus.values());
    }

    @Test
    void updateTask_appliesNonNullFields_whenSameOrganization() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);
        UpdateTaskRequest request = new UpdateTaskRequest(
                "New subject", "New desc", null, TaskPriority.HIGH, null,
                null, true, null, null, null, null
        );
        TaskDTO dto = sampleDto(id);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(dto);

        TaskDTO result = taskService.updateTask(id.toString(), request, user);

        assertThat(result).isEqualTo(dto);
        assertThat(task.getSubject()).isEqualTo("New subject");
        assertThat(task.getDescription()).isEqualTo("New desc");
        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        assertThat(task.getSendNotificationEmail()).isTrue();
        assertThat(task.getUpdatedBy()).isEqualTo(user);
    }

    @Test
    void updateTask_movesToBottomOfNewColumn_whenStatusChanges() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);
        UpdateTaskRequest request = new UpdateTaskRequest(
                null, null, TaskStatus.COMPLETED, null, null,
                null, null, null, null, null, null
        );

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.findByOrganizationIdAndStatusOrderByBoardPositionAsc(
                organization.getId(), TaskStatus.COMPLETED))
                .thenReturn(List.of(taskInOrg(UUID.randomUUID(), organization)));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(sampleDto(id));

        taskService.updateTask(id.toString(), request, user);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getBoardPosition()).isEqualTo(1);
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    void updateTask_throwsForbidden_whenTaskBelongsToAnotherOrganization() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, Organization.builder().id(UUID.randomUUID()).build());
        UpdateTaskRequest request = new UpdateTaskRequest(
                "x", null, null, null, null,
                null, null, null, null, null, null
        );

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateTask(id.toString(), request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_throwsNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        UpdateTaskRequest request = new UpdateTaskRequest(
                "x", null, null, null, null,
                null, null, null, null, null, null
        );
        when(taskRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(id.toString(), request, user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void moveTask_reordersWithinTargetColumn() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);
        Task other = taskInOrg(UUID.randomUUID(), organization);
        other.setStatus(TaskStatus.COMPLETED);
        other.setBoardPosition(0);
        MoveTaskRequest request = new MoveTaskRequest(TaskStatus.COMPLETED, 0);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.findByOrganizationIdAndStatusOrderByBoardPositionAsc(
                organization.getId(), TaskStatus.COMPLETED))
                .thenReturn(List.of(other));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(sampleDto(id));

        taskService.moveTask(id.toString(), request, user);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getBoardPosition()).isEqualTo(0);
        assertThat(other.getBoardPosition()).isEqualTo(1);
        assertThat(task.getUpdatedBy()).isEqualTo(user);
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    void moveTask_clampsPositionToColumnSize() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);
        Task other = taskInOrg(UUID.randomUUID(), organization);
        other.setStatus(TaskStatus.IN_PROGRESS);
        MoveTaskRequest request = new MoveTaskRequest(TaskStatus.IN_PROGRESS, 99);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));
        when(taskRepository.findByOrganizationIdAndStatusOrderByBoardPositionAsc(
                organization.getId(), TaskStatus.IN_PROGRESS))
                .thenReturn(List.of(other));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(sampleDto(id));

        taskService.moveTask(id.toString(), request, user);

        assertThat(other.getBoardPosition()).isEqualTo(0);
        assertThat(task.getBoardPosition()).isEqualTo(1);
    }

    @Test
    void deleteTask_deletes_whenSameOrganization() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, organization);

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        taskService.deleteTask(id.toString(), user);

        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTask_throwsForbidden_whenTaskBelongsToAnotherOrganization() {
        UUID id = UUID.randomUUID();
        Task task = taskInOrg(id, Organization.builder().id(UUID.randomUUID()).build());

        when(taskRepository.findById(id)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(id.toString(), user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(taskRepository, never()).delete(any());
    }
}
