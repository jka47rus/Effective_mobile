package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaskMapper {

    public TaskResponse fromTask(Task task) {

        String assignee = (task.getAssignee() != null) ? task.getAssignee().getEmail() : "";

        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle(task.getTitle());
        taskResponse.setDescription(task.getDescription());
        taskResponse.setStatus(task.getStatus());
        taskResponse.setPriority(task.getPriority());
        taskResponse.setAuthor(task.getAuthor().getEmail());
        taskResponse.setAssignee(assignee);
        taskResponse.setComments(task.getComments().stream().map(Comment::getText).collect(Collectors.toList()));

        return taskResponse;

    }

    public Page<TaskResponse> fromAll(Page<Task> tasks) {
        List<TaskResponse> taskResponses = tasks.stream()
                .map(this::fromTask)
                .collect(Collectors.toList());

        return new PageImpl<>(taskResponses, tasks.getPageable(), tasks.getTotalElements());
    }

}
