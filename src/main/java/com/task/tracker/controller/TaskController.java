package com.task.tracker.controller;

import com.task.tracker.dto.StatusDTO;
import com.task.tracker.dto.TaskDTO;
import com.task.tracker.exception.ForbiddenException;
import com.task.tracker.model.Task;
import com.task.tracker.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "API для управления задачами")
public class TaskController {

    private final TaskService taskService;

    @Operation(
            summary = "Получить список задач",
            description = "Возвращает список задач, принадлежащих текущему пользователю или все если это админ. Можно фильтровать по параметру `visible`.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список задач успешно получен", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Задачи не найдены", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<?> getTasks(
            Principal principal,
            @RequestParam(required = false)
            @Parameter(description = "Фильтр по видимости задач") Boolean visible) {
        try {
            return ResponseEntity.ok().body(taskService.getTasks(principal.getName(), visible));
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Получить задачу по ID",
            description = "Возвращает задачу по её ID, если она принадлежит текущему пользователю.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно найдена", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content)
            }
    )
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        try {
            return ResponseEntity.ok(taskService.getTaskById(taskId, userEmail));
        } catch (Throwable e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Создать новую задачу",
            description = "Создаёт новую задачу для текущего пользователя.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно создана", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Ошибка при создании задачи", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные новой задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Пример новой задачи",
                                            value = "{\"title\": \"New Task\", \"description\": \"Task description\", \"visibility\": true, \"statusId\": 1, \"priorityId\": 2, \"userId\": 3, \"executorsIds\": [3, 4], \"commentsIds\": []}"
                                    )
                            }
                    )
            )
    )
    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestBody
            @Parameter(description = "Данные новой задачи") TaskDTO taskDto,
            Principal principal) {
        Task createdTask = taskService.createTask(taskDto, principal.getName());
        return ResponseEntity.ok(createdTask);
    }

    @Operation(
            summary = "Обновить задачу",
            description = "Обновляет существующую задачу, принадлежащую текущему пользователю.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Ошибка при обновлении задачи", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Пример обновления задачи",
                                            value = "{\"title\": \"Updated Task\", \"description\": \"Updated description\", \"visibility\": false, \"statusId\": 2, \"priorityId\": 1, \"userId\": 3, \"executorsIds\": [4, 5], \"commentsIds\": [1, 2]}"
                                    )
                            }
                    )
            )
    )
    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            @RequestBody
            @Parameter(description = "Обновленные данные задачи") TaskDTO taskDto,
            Principal principal) {
        try {
            taskService.updateTask(taskId, taskDto, principal.getName());
            return ResponseEntity.ok("Success update");
        } catch (ForbiddenException | HibernateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Удалить задачу",
            description = "Удаляет задачу по её ID, если она принадлежит текущему пользователю.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "400", description = "Ошибка при удалении задачи", content = @Content)
            }
    )
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            Principal principal) {
        try {
            taskService.deleteTask(taskId, principal.getName());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновить статус задачи",
            description = "Изменяет статус задачи на основе переданных данных, если пользователь имеет доступ.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус успешно обновлен", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Task.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Ошибка при обновлении статуса", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый статус задачи",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = StatusDTO.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Пример обновления статуса",
                                            value = "{\"id\": 2}"
                                    )
                            }
                    )
            )
    )
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            @RequestBody
            @Parameter(description = "Новый статус задачи") StatusDTO statusDto,
            Authentication authentication) throws Throwable {
        String executorEmail = authentication.getName();
        Task updatedTask = taskService.updateTaskStatus(taskId, statusDto, executorEmail);
        return ResponseEntity.ok(updatedTask);
    }
}
