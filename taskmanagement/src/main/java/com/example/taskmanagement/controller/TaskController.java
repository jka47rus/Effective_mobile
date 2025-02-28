package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
@Tag(name = "Task Controller", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;


    @Operation(summary = "Создать задачу", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача создана"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody Task task, @AuthenticationPrincipal UserDetails authenticatedUser) {
        return new ResponseEntity<>(taskService.createTask(task, authenticatedUser.getUsername()), HttpStatus.CREATED);
    }


    @Operation(summary = "Обновить задачу", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        return ResponseEntity.ok(taskService.updateTask(id, updatedTask));
    }


    @Operation(summary = "Удалить задачу", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Задача удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Получить все задачи", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public PagedModel<TaskResponse> getAllTasks(Pageable pageable) {
        return new PagedModel<>(taskService.getAllTasks(pageable));
    }


    @Operation(summary = "Получить задачи по исполнителю", description = "Доступно для USER и ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/assignee")
    public PagedModel<TaskResponse> getTasksByAssignee(@RequestParam Long userId, Pageable pageable) {
        return new PagedModel<>(taskService.getTasksByAssignee(userId, pageable));
    }

    @Operation(summary = "Получить задачи по автору", description = "Доступно для USER и ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/author")
    public PagedModel<TaskResponse> getTasksByAuthor(@RequestParam Long userId, Pageable pageable) {
        return new PagedModel<>(taskService.getTasksByAuthor(userId, pageable));
    }


    @Operation(summary = "Изменить приоритет задачи", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приоритет задачи изменен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> updatePriority(@PathVariable Long id,
                                                       @RequestParam Task.Priority newPriority,
                                                       @AuthenticationPrincipal UserDetails authenticatedUser) {
        return ResponseEntity.ok(taskService.updatePriority(id, newPriority, authenticatedUser.getUsername()));
    }


    @Operation(summary = "Назначить исполнителя задачи", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель назначен"),
            @ApiResponse(responseCode = "404", description = "Задача или пользователь не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/assign")
    public ResponseEntity<TaskResponse> assignTask(@PathVariable Long id, @RequestParam Long assigneeId) {
        return ResponseEntity.ok(taskService.assignTask(id, assigneeId));
    }


    @Operation(summary = "Изменить статус задачи", description = "Доступно для USER и ADMIN (только для своих задач)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус задачи изменен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                     @RequestParam Task.Status newStatus,
                                                     @AuthenticationPrincipal UserDetails authenticatedUser) {
        return ResponseEntity.ok(taskService.updateStatus(id, newStatus, authenticatedUser.getUsername()));
    }

    @Operation(summary = "Добавить комментарий к задаче", description = "Доступно для USER и ADMIN (только для своих задач)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий добавлен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/{id}/comment")
    public ResponseEntity<Comment> addComment(@PathVariable Long id,
                                              @RequestParam String comment,
                                              @AuthenticationPrincipal UserDetails authenticatedUser) {
        return ResponseEntity.ok(taskService.addComment(id, comment, authenticatedUser.getUsername()));
    }


}