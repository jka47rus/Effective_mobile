package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskResponse;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskMapper taskMapper;

    private User user;
    private Task task;
    private TaskResponse taskResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("test@example.com");
        user.setRole(User.Role.ADMIN);

        task = new Task();
        task.setId(1L);
        task.setAuthor(user);

        taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        taskResponse.setTitle("Test Task");
        taskResponse.setAuthor(user.getEmail());
    }

    @Test
    public void createTask_ShouldReturnTaskResponse() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTask(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.createTask(task, user.getEmail());

        assertEquals(taskResponse, result);
        verify(taskRepository).save(task);
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTaskResponse() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTask(task)).thenReturn(taskResponse);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        taskService.updateTask(task.getId(), updatedTask);

        assertEquals("Updated Task", task.getTitle());
        verify(taskRepository).save(task);
    }

    @Test
    public void deleteTask_ShouldDeleteTask() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        taskService.deleteTask(task.getId());

        verify(taskRepository).delete(task);
    }

    @Test
    public void getAllTasks_ShouldReturnPageOfTaskResponse() {
        Page<Task> taskPage = new PageImpl<>(List.of(task));
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.fromAll(taskPage)).thenReturn(new PageImpl<>(List.of(taskResponse)));

        Page<TaskResponse> result = taskService.getAllTasks(Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findAll(any(Pageable.class));
    }

    @Test
    public void getTasksByAuthor_ShouldReturnPageOfTaskResponse() {
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(taskRepository.findByAuthorId(eq(user.getId()), any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.fromAll(taskPage)).thenReturn(new PageImpl<>(List.of(taskResponse)));

        Page<TaskResponse> result = taskService.getTasksByAuthor(user.getId(), Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
        verify(taskRepository).findByAuthorId(eq(user.getId()), any(Pageable.class));
    }

    @Test
    public void assignTask_ShouldReturnAssignedTaskResponse() {
        User assignee = new User();
        assignee.setId(2L);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTask(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.assignTask(task.getId(), assignee.getId());

        assertEquals(taskResponse, result);
        assertEquals(assignee, task.getAssignee());
    }

    @Test
    public void updatePriority_ShouldReturnUpdatedTaskResponse() {

        task.setPriority(Task.Priority.HIGH);
        taskResponse.setPriority(task.getPriority());

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTask(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.updatePriority(task.getId(), Task.Priority.HIGH, user.getEmail());

        assertEquals(result.getPriority(), task.getPriority());
        verify(taskRepository).save(task);


    }


    @Test
    public void updatePriority_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            taskService.updatePriority(task.getId(), Task.Priority.HIGH, user.getEmail());
        });
    }

    @Test
    public void updatePriority_ShouldThrowException() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThrows(RuntimeException.class, () -> {
            taskService.updatePriority(task.getId(), Task.Priority.HIGH, user.getEmail());
        });
    }

    @Test
    public void addComment_ShouldReturnComment() {
        String commentText = "This is a comment";
        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setAuthor(user);
        comment.setTask(task);

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = taskService.addComment(task.getId(), commentText, user.getEmail());

        assertNotNull(result);
        assertEquals(commentText, result.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    public void addComment_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            taskService.addComment(task.getId(), "Comment text", user.getEmail());
        });
    }


    @Test
    public void updateStatus_ShouldReturnUpdatedTaskResponse() {

        task.setStatus(Task.Status.IN_PROGRESS);
        taskResponse.setStatus(task.getStatus());

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTask(task)).thenReturn(taskResponse);


        TaskResponse result = taskService.updateStatus(task.getId(), Task.Status.IN_PROGRESS, user.getEmail());

        assertEquals(result.getStatus(), task.getStatus());
        verify(taskRepository).save(task);
    }

    @Test
    public void updateStatus_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            taskService.updateStatus(task.getId(), Task.Status.IN_PROGRESS, user.getEmail());
        });
    }


}