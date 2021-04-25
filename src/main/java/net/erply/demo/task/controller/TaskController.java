package net.erply.demo.task.controller;

import static net.erply.demo.base.entity.ErrorCode.WRONG_TASK_ID;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.log4j.Log4j2;
import net.erply.demo.base.api.ErrorDto;
import net.erply.demo.base.exception.AddTaskBadResponseException;
import net.erply.demo.base.exception.DeleteTaskBadResponseException;
import net.erply.demo.base.exception.EditTaskBadResponseException;
import net.erply.demo.base.exception.TaskException;
import net.erply.demo.task.api.TaskDto;
import net.erply.demo.task.service.DefaultTaskServiceImpl;
import net.erply.demo.task.service.TaskService;

/**
 * Task Application API controller
 * endpoints for following actions:
 * • Add a task
 * • Change a task
 * • Delete a task
 * • Task completed
 *
 */

@Log4j2
@RestController
@RequestMapping(value = "/api/v1/task", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(DefaultTaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @ApiOperation(value = "Returns added new task", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned when task successfully created", response = TaskDto.class),
            @ApiResponse(code = 400, message = "Returned when task creation failed", response = ErrorDto.class),
    })
    @ResponseBody
    @PostMapping(path = "/add", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskDto addTask (@RequestBody TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @ApiOperation(value = "Deletes task by id", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned when task successfully deleted"),
            @ApiResponse(code = 400, message = "Returned when task deletion failed", response = ErrorDto.class),
    })
    @ResponseBody
    @DeleteMapping(path = "/delete/{id}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteTask (@PathVariable Integer id) {
        taskService.deleteTask(id);
    }

    @ApiOperation(value = "Changes existing task", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned when task successfully edited", response = TaskDto.class),
            @ApiResponse(code = 400, message = "Returned when task edit failed", response = ErrorDto.class),
    })
    @ResponseBody
    @PostMapping(path = "/edit/{id}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskDto changeTask (@PathVariable Integer id, @RequestBody TaskDto taskDto) {
        return taskService.editTask(id, taskDto);
    }

    @ApiOperation(value = "Complete existing task", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned when task successfully marked as completed"),
            @ApiResponse(code = 400, message = "Returned when task completion failed", response = ErrorDto.class),
    })
    @ResponseBody
    @PostMapping(path = "/complete/{id}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void completeTask (@PathVariable Integer id) {
        taskService.completeTask(id);
    }


    @ApiOperation(value = "List of all tasks", produces = "application/json", consumes = "application/json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Returned list of tasks", response = List.class)
    })
    @ResponseBody
    @GetMapping(path = "/list", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TaskDto> TaskList () {
        return taskService.list();
    }

    @ExceptionHandler({ EditTaskBadResponseException.class,
            DeleteTaskBadResponseException.class,
            AddTaskBadResponseException.class   })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleFailedTaskOperation(TaskException ex) {
        return  ErrorDto.builder()
                .code(ex.getCode())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleTaskOperationWithWrongId(MethodArgumentTypeMismatchException ex) {
        return  ErrorDto.builder()
                .code(WRONG_TASK_ID.name())
                .build();
    }
}
