package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
