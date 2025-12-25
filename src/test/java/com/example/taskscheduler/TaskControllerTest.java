package com.example.taskscheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateTask() throws Exception {
        Task task = new Task("Buy milk");
        task.setCompleted(false);
        task.setCreatedAt(LocalDate.now());

        when(taskService.createTask(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("Buy milk"));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        Task task1 = new Task("Buy milk");
        Task task2 = new Task("Walk dog");
        when(taskService.getAllTasks()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task updated = new Task("Buy milk");
        updated.setCompleted(true);
        when(taskService.updateTask(eq("Buy milk"), any(Task.class))).thenReturn(updated);

        mockMvc.perform(put("/tasks/Buy milk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask("Buy milk");

        mockMvc.perform(delete("/tasks/Buy milk"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        when(taskService.updateTask(eq("missing"), any(Task.class)))
                .thenThrow(new NoSuchElementException());

        Task task = new Task("missing");
        task.setCompleted(true);

        mockMvc.perform(put("/tasks/missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isNotFound());
    }
}