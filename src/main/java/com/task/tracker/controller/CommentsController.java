package com.task.tracker.controller;

import com.task.tracker.dto.CommentDTO;
import com.task.tracker.model.Comment;
import com.task.tracker.service.CommentService;
import com.task.tracker.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Комментарии", description = "API для управления комментариями к задачам")
public class CommentsController {

    private final CommentService commentService;

    @Operation(
            summary = "Получить все комментарии пользователя",
            description = "Возвращает список всех комментариев, сделанных текущим пользователем.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарии успешно получены", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Comment.class)
                    ))
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllComments(Principal principal, Pageable pageable) {
        return ResponseEntity.ok().body(
                PaginationUtils.toPage(
                        commentService.getComments(principal.getName()),
                        pageable
                )
        );
    }

    @Operation(
            summary = "Получить все комментарии задачи",
            description = "Возвращает список всех комментариев для указанной задачи, если пользователь имеет доступ.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарии задачи успешно получены", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Comment.class)
                    )),
                    @ApiResponse(responseCode = "404", description = "Комментарии задачи не найдены", content = @Content)
            }
    )
    @GetMapping("/{taskId}")
    public ResponseEntity<?> getAllTaskComments(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            Principal principal,
            Pageable pageable) {
        return ResponseEntity.ok().body(
                PaginationUtils.toPage(
                    commentService.getAllByTaskId(taskId, principal.getName()),
                    pageable
                )
        );
    }

    @Operation(
            summary = "Добавить комментарий к задаче",
            description = "Добавляет новый комментарий к указанной задаче.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Comment.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Ошибка при добавлении комментария", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового комментария",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Пример нового комментария",
                                            value = "{\"content\": \"This is a new comment.\"}"
                                    )
                            }
                    )
            )
    )
    @PostMapping("/{taskId}")
    public ResponseEntity<?> addComment(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            @RequestBody CommentDTO commentDto,
            Principal principal) throws Throwable {
        String commenterEmail = principal.getName();
        try {
            Comment comment = commentService.addComment(taskId, commentDto, commenterEmail);
            return ResponseEntity.ok(comment);
        } catch (HibernateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(
            summary = "Ответить на комментарий",
            description = "Добавляет ответ на указанный комментарий для задачи.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ответ на комментарий успешно добавлен", content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Comment.class)
                    )),
                    @ApiResponse(responseCode = "400", description = "Ошибка при добавлении ответа", content = @Content)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные ответа на комментарий",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommentDTO.class),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                            name = "Пример ответа на комментарий",
                                            value = "{\"content\": \"This is a reply to your comment.\"}"
                                    )
                            }
                    )
            )
    )
    @PostMapping("/{taskId}/{parentCommentId}/reply")
    public ResponseEntity<?> replyComment(
            @PathVariable
            @Parameter(description = "ID задачи", example = "1") Long taskId,
            @PathVariable
            @Parameter(description = "ID родительского комментария", example = "2") Long parentCommentId,
            @RequestBody CommentDTO commentDto,
            Authentication authentication) throws Throwable {
        String commenterEmail = authentication.getName();
        try {
            Comment comment = commentService.replyOnComment(taskId, parentCommentId, commentDto, commenterEmail);
            return ResponseEntity.ok(comment);
        } catch (HibernateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}
