package com.task.tracker.controller;

import com.task.tracker.dto.StatusDTO;
import com.task.tracker.exception.ForbiddenException;
import com.task.tracker.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "Статус", description = "API для управления статусами")
public class StatusController {

    private final StatusService statusService;

    @GetMapping
    @Operation(summary = "Получить все статусы", description = "Получение списка всех статусов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = StatusDTO.class),
                            examples = @ExampleObject(value = "[{\"id\": 1, \"title\": \"В процессе\", \"isGlobal\": true, \"userId\": 101}, {\"id\": 2, \"title\": \"Завершено\", \"isGlobal\": false, \"userId\": 102}]"))),
            @ApiResponse(responseCode = "404", description = "Статусы не найдены")
    })
    public ResponseEntity<?> getAllStatus() {
        return ResponseEntity.ok().body(statusService.getAll());
    }

    @GetMapping("/{statusId}")
    @Operation(summary = "Получить статус по ID", description = "Получение статуса по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = StatusDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"В процессе\", \"isGlobal\": true, \"userId\": 101}"))),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    public ResponseEntity<?> getById(@Parameter(description = "ID статуса для получения") @PathVariable Long statusId) {
        return ResponseEntity.ok().body(statusService.getById(statusId));
    }

    @PostMapping
    @Operation(summary = "Добавить новый статус", description = "Создание нового статуса. isGlobal - определяет, статус является локальным для данного пользователя или его могут использовать все пользователи и связан с userId, глобальные статусы может добавлять только админ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = StatusDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 3, \"title\": \"Новый статус\", \"isGlobal\": false, \"userId\": 103}"))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод")
    })
    public ResponseEntity<?> addStatus(@RequestBody @Schema(implementation = StatusDTO.class, example = "{\"title\": \"Новый статус\", \"isGlobal\": false, \"userId\": 1}") StatusDTO statusDTO, Principal principal) {
        return ResponseEntity.ok().body(statusService.add(statusDTO, principal.getName()));
    }

    @PutMapping("/{statusId}")
    @Operation(summary = "Обновить существующий статус", description = "Обновление статуса по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = StatusDTO.class),
                            examples = @ExampleObject(value = "{\"id\": 1, \"title\": \"Обновленный статус\", \"isGlobal\": true, \"userId\": 101}"))),
            @ApiResponse(responseCode = "400", description = "Неверный ввод"),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    public ResponseEntity<?> updateStatus(@Parameter(description = "ID статуса для обновления") @PathVariable Long statusId, @RequestBody @Schema(implementation = StatusDTO.class, example = "{\"title\": \"Обновленный статус\", \"isGlobal\": true, \"userId\": null}") StatusDTO statusDTO) {
        return ResponseEntity.ok().body(statusService.update(statusId, statusDTO));
    }

    @DeleteMapping("/{statusId}")
    @Operation(summary = "Удалить статус", description = "Удаление статуса по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешная операция"),
            @ApiResponse(responseCode = "404", description = "Статус не найден")
    })
    public ResponseEntity<?> deleteStatus(@Parameter(description = "ID статуса для удаления") @PathVariable Long statusId, Principal principal) {
        try {
            statusService.delete(statusId, principal.getName());
            return ResponseEntity.noContent().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
