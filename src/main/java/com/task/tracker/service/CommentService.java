package com.task.tracker.service;

import com.task.tracker.dto.CommentDTO;
import com.task.tracker.exception.ForbiddenException;
import com.task.tracker.exception.NotFoundException;
import com.task.tracker.model.Comment;
import com.task.tracker.model.Task;
import com.task.tracker.model.User;
import com.task.tracker.repository.CommentRepository;
import com.task.tracker.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public void add(Comment comment) {
        commentRepository.save(comment);
    }

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    public Optional<Comment> getById(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getAllByEmail(String email) {
        return commentRepository.findAllByUserEmail(email);
    }

    public List<Comment> getAllByTaskId(Long taskId, String email) {
        List<Comment> comments = commentRepository.findAllByTaskId(taskId);

        if (userService.isAdminByEmail(email)) return comments;

        return comments.stream()
                .filter(x-> x.getUser().getEmail().equals(email))
                .collect(Collectors.toList());
    }

    public List<Comment> getComments(String email) {
        if (userService.isAdminByEmail(email)) {
            return getAll();
        } else {
            return getAllByEmail(email);
        }
    }


    public Comment addComment(Long taskId, CommentDTO commentDto, String commenterEmail) throws ForbiddenException {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (task.getVisibility().equals(Boolean.FALSE)) throw new ForbiddenException();

        User commenter = userService.getUserByEmail(commenterEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(commenter);
        comment.setContent(commentDto.getContent());

        if (commentDto.getParentId() != null) {

            Comment parent = getById(commentDto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Comment not found"));
            comment.setParent(parent);
        }

        if (commentDto.getRepliesIds() != null) {
            List<Comment> replies = commentDto.getRepliesIds()
                    .stream()
                    .map(this::getById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            comment.setReplies(replies);
        }

        add(comment);
        return comment;
    }

    public Comment replyOnComment(Long taskId, Long parentId, CommentDTO commentDto, String commenterEmail) throws ForbiddenException {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User commenter = userService.getUserByEmail(commenterEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Comment parentComment = getById(parentId)
                .orElseThrow(() -> new NotFoundException("Parent comment not found"));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(commenter);
        comment.setContent(commentDto.getContent());
        comment.setParent(parentComment);

        add(comment);
        return comment;
    }
}
