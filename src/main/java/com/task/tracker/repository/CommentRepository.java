package com.task.tracker.repository;

import com.task.tracker.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByUserEmail(String email);
    List<Comment> findAllByTaskId(Long id);
}
