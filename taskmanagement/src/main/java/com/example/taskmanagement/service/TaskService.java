package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;

    public TaskResponse createTask(Task task, String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        task.setAuthor(user);
        taskRepository.save(task);

        return taskMapper.fromTask(task);
    }

    public TaskResponse updateTask(Long taskId, Task updatedTask) {
        Task task = taskRepository.findById(taskId).orElseThrow();

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());
        taskRepository.save(task);

        return taskMapper.fromTask(task);
    }

    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow();
        taskRepository.delete(task);
    }

    public Page<TaskResponse> getAllTasks(Pageable pageable) {
        return taskMapper.fromAll(taskRepository.findAll(pageable));
    }


    public Page<TaskResponse> getTasksByAuthor(Long authorId, Pageable pageable) {
        User user = userRepository.findById(authorId).orElseThrow();
        return taskMapper.fromAll(taskRepository.findByAuthorId(authorId, pageable));
    }

    public Page<TaskResponse> getTasksByAssignee(Long assigneeId, Pageable pageable) {
        User user = userRepository.findById(assigneeId).orElseThrow();
        return taskMapper.fromAll(taskRepository.findByAssigneeId(assigneeId, pageable));
    }


    public TaskResponse assignTask(Long taskId, Long assigneeId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        User assignee = userRepository.findById(assigneeId).orElseThrow(() -> new RuntimeException("User not found"));

        task.setAssignee(assignee);
        return taskMapper.fromTask(taskRepository.save(task));
    }

    public TaskResponse updatePriority(Long taskId, Task.Priority newPriority, String email) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        if (!isAuthorized(email, task)) {
            throw new RuntimeException("Access denied");
        }

        task.setPriority(newPriority);
        return taskMapper.fromTask(taskRepository.save(task));
    }


    public Comment addComment(Long taskId, String text, String email) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        if (!isAuthorized(email, task)) {
            throw new RuntimeException("Access denied");
        }

        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(userRepository.findByEmail(email).orElseThrow());
        comment.setTask(task);
        commentRepository.save(comment);

        return comment;
    }


    public TaskResponse updateStatus(Long taskId, Task.Status newStatus, String email) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));

        if (!isAuthorized(email, task)) {
            throw new RuntimeException("Access denied");
        }

        task.setStatus(newStatus);
        return taskMapper.fromTask(taskRepository.save(task));
    }


    public boolean isAuthorized(String email, Task task) {
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getRole() == User.Role.ADMIN || task.getAssignee().getEmail().equals(user.getEmail());

    }


}
