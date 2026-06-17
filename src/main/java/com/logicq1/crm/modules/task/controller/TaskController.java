package com.logicq1.crm.modules.task.controller;

import com.logicq1.crm.modules.task.dto.*;
import com.logicq1.crm.modules.task.service.TaskService;
import com.logicq1.crm.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTask(
            @AuthenticationPrincipal User user,
            @RequestBody CreateTaskRequest request
    ) {
        TaskDTO task = taskService.createTask(request, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "Task Created Successfully");
        response.put("task", task);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> listTasks(@AuthenticationPrincipal User user) {
        List<TaskDTO> tasks = taskService.listTasks(user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("tasks", tasks);
        return ResponseEntity.ok(response);
    }

    /** Kanban view: tasks grouped into columns by status. */
    @GetMapping("/board")
    public ResponseEntity<Map<String, Object>> getBoard(@AuthenticationPrincipal User user) {
        List<TaskBoardColumn> board = taskService.getBoard(user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("board", board);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTaskById(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        TaskDTO task = taskService.getTaskById(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("task", task);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTask(
            @PathVariable String id,
            @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal User user
    ) {
        TaskDTO task = taskService.updateTask(id, request, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "Task Updated Successfully");
        response.put("task", task);
        return ResponseEntity.ok(response);
    }

    /** Move/reorder a card on the kanban board (drag-and-drop). */
    @PatchMapping("/{id}/move")
    public ResponseEntity<Map<String, Object>> moveTask(
            @PathVariable String id,
            @RequestBody MoveTaskRequest request,
            @AuthenticationPrincipal User user
    ) {
        TaskDTO task = taskService.moveTask(id, request, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "Task Moved Successfully");
        response.put("task", task);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTask(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        taskService.deleteTask(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("message", "Task Deleted Successfully");
        return ResponseEntity.ok(response);
    }
}
