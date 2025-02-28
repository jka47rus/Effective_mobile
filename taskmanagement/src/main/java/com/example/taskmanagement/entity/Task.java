package com.example.taskmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be null")
    private String title;

    @Size(max = 500, message = "Description too long")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @ManyToOne
    private User author;

    @ManyToOne
    private User assignee;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public enum Status {
        WAITING, IN_PROGRESS, COMPLETED
    }

    public enum Priority {
        HIGH, MEDIUM, LOW
    }

}

