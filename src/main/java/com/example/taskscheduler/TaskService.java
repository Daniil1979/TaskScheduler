package com.example.taskscheduler;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task with this ID already exists");
        }
        task.setCreatedAt(LocalDate.now());
        tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(String id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task not found");
        }
        updatedTask.setId(id);
        tasks.put(id, updatedTask);
        return updatedTask;
    }

    public void deleteTask(String id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task not found");
        }
        tasks.remove(id);
    }

    public Task getTaskById(String id) {
        return tasks.get(id);
    }
}