package com.example.taskmanagement.dto;

import com.example.taskmanagement.entity.Task;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private Task.Status status;

    private Task.Priority priority;

    private String author;

    private String assignee;

    private List<String> comments;

}
