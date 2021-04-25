package net.erply.demo.task.service;

import java.util.List;

import net.erply.demo.task.api.TaskDto;

public interface TaskService {

    TaskDto addTask(TaskDto taskDto);

    void deleteTask(Integer id);

    TaskDto editTask(Integer id, TaskDto taskDto);

    void completeTask(Integer id);

    List<TaskDto> list();
}
